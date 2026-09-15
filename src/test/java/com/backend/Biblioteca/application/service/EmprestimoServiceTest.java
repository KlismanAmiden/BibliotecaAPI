package com.backend.Biblioteca.application.service;

import com.backend.Biblioteca.application.dto.request.EmprestimoRequestDTO;
import com.backend.Biblioteca.application.dto.response.EmprestimoResponseDTO;
import com.backend.Biblioteca.domain.enums.StatusEmprestimo;
import com.backend.Biblioteca.domain.enums.StatusExemplar;
import com.backend.Biblioteca.domain.model.Emprestimo;
import com.backend.Biblioteca.domain.model.Exemplar;
import com.backend.Biblioteca.domain.model.Livro;
import com.backend.Biblioteca.domain.model.Usuario;
import com.backend.Biblioteca.infrastructure.repository.EmprestimoRepository;
import com.backend.Biblioteca.infrastructure.repository.ExemplarRepository;
import com.backend.Biblioteca.infrastructure.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmprestimoServiceTest  {

    @Mock
    private EmprestimoRepository repository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ExemplarRepository exemplarRepository;

    @InjectMocks
    private EmprestimoService service;

    private Usuario criarUsuario(Long id) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNome("Kl");
        usuario.setEmail("kl@teste.com");
        usuario.setSenha("senha123");
        usuario.setTelefone("71999999999");
        usuario.setDataCadastro(LocalDateTime.now());
        usuario.setAtivo(true);
        return usuario;
    }

    private Exemplar criarExemplar(Long id, StatusExemplar status) {
        Exemplar exemplar = new Exemplar();
        exemplar.setId(id);
        exemplar.setLivro(new Livro());
        exemplar.setStatus(status);
        return exemplar;
    }

    private Emprestimo criarEmprestimo(Long id, Usuario usuario, Set<Exemplar> exemplares,
                                       LocalDateTime dataPrevistaDevolucao, StatusEmprestimo status) {
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setId(id);
        emprestimo.setUsuario(usuario);
        emprestimo.setExemplares(exemplares);
        emprestimo.setDataEmprestimo(LocalDateTime.now().minusDays(5));
        emprestimo.setDataPrevistaDevolucao(dataPrevistaDevolucao);
        emprestimo.setStatus(status);
        emprestimo.setMulta(BigDecimal.ZERO);
        return emprestimo;
    }

    private EmprestimoRequestDTO criarDto(Long usuarioId, Set<Long> exemplaresIds, LocalDateTime dataPrevista) {
        return new EmprestimoRequestDTO(usuarioId, exemplaresIds, dataPrevista);
    }
    @Test
    void deveListarTodosOsEmprestimos() {
        Usuario usuario = criarUsuario(1L);
        Emprestimo emprestimo = criarEmprestimo(1L, usuario, Set.of(criarExemplar(1L, StatusExemplar.EMPRESTADO)),
                LocalDateTime.now().plusDays(7), StatusEmprestimo.ATIVO);

        when(repository.findAll()).thenReturn(List.of(emprestimo));

        List<EmprestimoResponseDTO> resultado = service.listarTodos();

        assertEquals(1, resultado.size());
        verify(repository).findAll();
    }
}
