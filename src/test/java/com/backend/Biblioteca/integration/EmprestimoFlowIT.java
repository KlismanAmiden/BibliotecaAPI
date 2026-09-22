package com.backend.Biblioteca.integration;

import com.backend.Biblioteca.application.dto.request.*;
import com.backend.Biblioteca.application.dto.response.*;
import com.backend.Biblioteca.domain.enums.Role;
import com.backend.Biblioteca.domain.model.Usuario;
import com.backend.Biblioteca.infrastructure.config.TestcontainersConfiguration;
import com.backend.Biblioteca.infrastructure.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("it")
class EmprestimoFlowIT {

    @Autowired
    private RestTestClient client;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private String usuarioToken;
    private Long usuarioId;

    @BeforeEach
    void seedAdminERegistraUsuario() {

        String adminEmail = "admin-" + UUID.randomUUID() + "@email.com";
        Usuario admin = new Usuario();
        admin.setNome("Admin Seed");
        admin.setEmail(adminEmail);
        admin.setTelefone("71999999999");
        admin.setSenha(passwordEncoder.encode("admin123"));
        admin.setDataCadastro(LocalDateTime.now());
        admin.setAtivo(true);
        admin.setRole(Role.ADMIN);
        usuarioRepository.save(admin);

        adminToken = login(adminEmail, "admin123");

        String usuarioEmail = "leitor-" + UUID.randomUUID() + "@email.com";
        UsuarioResponseDTO usuarioCriado = client.post().uri("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new UsuarioRequestDTO("Leitor Teste", usuarioEmail, "senha123", "71988888888"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UsuarioResponseDTO.class)
                .returnResult().getResponseBody();

        usuarioId = usuarioCriado.id();
        usuarioToken = login(usuarioEmail, "senha123");
    }

    private String login(String email, String senha) {
        LoginResponseDTO resposta = client.post().uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new LoginRequestDTO(email, senha))
                .exchange()
                .expectStatus().isOk()
                .expectBody(LoginResponseDTO.class)
                .returnResult().getResponseBody();
        return resposta.token();
    }

    @Test
    void fluxoCompletoDeEmprestimoComLimiteEDevolucao() {

        // ADMIN monta o catálogo
        Long generoId = client.post().uri("/api/generos")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new GeneroRequestDTO("Romance", "Ficção narrativa"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(GeneroResponseDTO.class)
                .returnResult().getResponseBody().id();

        Long autorId = client.post().uri("/api/autores")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new AutorRequestDTO("Machado de Assis", "Escritor brasileiro", 1839, "Brasileira"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(AutorResponseDTO.class)
                .returnResult().getResponseBody().id();

        Long livroId = client.post().uri("/api/livros")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new LivroRequestDTO("Dom Casmurro", "978-85-359-0277-0", 1899,
                        "Um clássico", "Editora XPTO", Set.of(autorId), Set.of(generoId)))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(LivroResponseDTO.class)
                .returnResult().getResponseBody().id();

        // 3 exemplares disponíveis pro mesmo livro
        Long exemplar1 = criarExemplar(livroId);
        Long exemplar2 = criarExemplar(livroId);
        Long exemplar3 = criarExemplar(livroId);

        // usuário pega 3 empréstimos - todos devem passar
        Long emprestimo1 = criarEmprestimo(exemplar1);
        criarEmprestimo(exemplar2);
        criarEmprestimo(exemplar3);
        assertThat(emprestimo1).isNotNull();

        // 4º empréstimo deve ser barrado pelo limite, antes mesmo de olhar o exemplar
        client.post().uri("/api/emprestimos")
                .header("Authorization", "Bearer " + usuarioToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new EmprestimoRequestDTO(usuarioId, Set.of(exemplar1), LocalDateTime.now().plusDays(7)))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Usuário atingiu o limite de 3 empréstimos ativos");

        // devolve o primeiro, como BIBLIOTECARIO/ADMIN
        client.patch().uri("/api/emprestimos/" + emprestimo1 + "/devolver")
                .header("Authorization", "Bearer " + adminToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("DEVOLVIDO")
                .jsonPath("$.multa").isEqualTo(0);
    }
    @Test
    void usuarioComumNaoPodeCriarEmprestimoParaOutroUsuario() {

        String outroEmail = "outro-" + UUID.randomUUID() + "@email.com";
        client.post().uri("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new UsuarioRequestDTO("Outro", outroEmail, "senha123", "71977777777"))
                .exchange()
                .expectStatus().isCreated();

        // usuarioToken tenta criar empréstimo em nome do "outro" -> deve ser barrado no service
        client.post().uri("/api/emprestimos")
                .header("Authorization", "Bearer " + usuarioToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new EmprestimoRequestDTO(999999L, Set.of(1L), LocalDateTime.now().plusDays(7)))
                .exchange()
                .expectStatus().is4xxClientError();
    }

    //-------------------------------Métodos auxiliares privados-----------------------------------------

    private Long criarExemplar(Long livroId) {
        return client.post().uri("/api/exemplares")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ExemplarRequestDTO(livroId))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ExemplarResponseDTO.class)
                .returnResult().getResponseBody().id();
    }

    private Long criarEmprestimo(Long exemplarId) {
        return client.post().uri("/api/emprestimos")
                .header("Authorization", "Bearer " + usuarioToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new EmprestimoRequestDTO(usuarioId, Set.of(exemplarId), LocalDateTime.now().plusDays(7)))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(EmprestimoResponseDTO.class)
                .returnResult().getResponseBody().id();
    }
}
