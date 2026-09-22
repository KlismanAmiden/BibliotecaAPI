package com.backend.Biblioteca.integration;

import com.backend.Biblioteca.application.dto.request.LoginRequestDTO;
import com.backend.Biblioteca.application.dto.request.UsuarioRequestDTO;
import com.backend.Biblioteca.application.dto.response.LoginResponseDTO;
import com.backend.Biblioteca.infrastructure.config.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("it")
class AuthFlowIT {

    @Autowired
    private RestTestClient client;

    private String emailUnico(){
        return "usuario-" + UUID.randomUUID() + "@email.com";
    }
    @Test
    void deveCadastrarLogarEAcessarEndpointProtegidoComTokenReal() {

        String email = emailUnico();
        UsuarioRequestDTO cadastro = new UsuarioRequestDTO("Fulano de Tal", email, "senha123", "71999999999");

        var usuarioCriado = client.post().uri("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .body(cadastro)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(com.backend.Biblioteca.application.dto.response.UsuarioResponseDTO.class)
                .returnResult().getResponseBody();

        assertThat(usuarioCriado).isNotNull();
        Long usuarioId = usuarioCriado.id();

        LoginRequestDTO login = new LoginRequestDTO(email, "senha123");
        LoginResponseDTO tokenResponse = client.post().uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(login)
                .exchange()
                .expectStatus().isOk()
                .expectBody(LoginResponseDTO.class)
                .returnResult().getResponseBody();

        assertThat(tokenResponse).isNotNull();
        assertThat(tokenResponse.token()).isNotBlank();

        client.get().uri("/api/usuarios/" + usuarioId)
                .header("Authorization", "Bearer " + tokenResponse.token())
                .exchange()
                .expectStatus().isOk();
    }
    @Test
    void deveRetornar401AoAcessarEndpointProtegidoSemToken() {

        client.get().uri("/api/usuarios")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void loginComSenhaErradaDeveRetornar400() {

        String email = emailUnico();
        client.post().uri("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new UsuarioRequestDTO("Fulano", email, "senhaCerta", "71999999999"))
                .exchange()
                .expectStatus().isCreated();

        client.post().uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new LoginRequestDTO(email, "senhaErrada"))
                .exchange()
                .expectStatus().isBadRequest();
    }
}
