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
import com.backend.Biblioteca.web.exception.BadRequestException;
import com.backend.Biblioteca.web.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    @Test
    void deveBuscarEmprestimoPorId() {
        Usuario usuario = criarUsuario(1L);
        Emprestimo emprestimo = criarEmprestimo(1L, usuario, Set.of(criarExemplar(1L, StatusExemplar.EMPRESTADO)),
                LocalDateTime.now().plusDays(7), StatusEmprestimo.ATIVO);

        when(repository.findById(1L)).thenReturn(Optional.of(emprestimo));

        EmprestimoResponseDTO resultado = service.buscarPorId(1L);

        assertEquals(1L, resultado.id());
        assertEquals(1L, resultado.usuarioId());
    }
    @Test
    void deveLancarExcecaoQuandoEmprestimoNaoEncontrado() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.buscarPorId(99L)
        );

        assertEquals("Empréstimo não encontrado com id: 99", exception.getMessage());
    }
    @Test
    void deveListarEmprestimosPorUsuario() {
        Usuario usuario = criarUsuario(1L);
        Emprestimo emprestimo = criarEmprestimo(1L, usuario, Set.of(criarExemplar(1L, StatusExemplar.EMPRESTADO)),
                LocalDateTime.now().plusDays(7), StatusEmprestimo.ATIVO);

        when(repository.findByUsuarioId(1L)).thenReturn(List.of(emprestimo));

        List<EmprestimoResponseDTO> resultado = service.listarPorUsuario(1L);

        assertEquals(1, resultado.size());
        verify(repository).findByUsuarioId(1L);
    }

    @Test
    void deveCriarEmprestimoComSucesso() {
        Usuario usuario = criarUsuario(1L);
        Exemplar exemplar = criarExemplar(1L, StatusExemplar.DISPONIVEL);
        EmprestimoRequestDTO dto = criarDto(1L, Set.of(1L), LocalDateTime.now().plusDays(14));

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(repository.findByUsuarioIdAndStatus(1L, StatusEmprestimo.ATIVO)).thenReturn(List.of());
        when(exemplarRepository.findAllById(dto.exemplaresIds())).thenReturn(List.of(exemplar));
        when(repository.save(any(Emprestimo.class))).thenAnswer(invocation -> {
            Emprestimo salvo = invocation.getArgument(0);
            salvo.setId(10L);
            return salvo;
        });

        EmprestimoResponseDTO response = service.criar(dto);

        assertNotNull(response);
        assertEquals(10L, response.id());
        assertEquals(StatusEmprestimo.ATIVO, response.status());
        assertEquals(0, BigDecimal.ZERO.compareTo(response.multa()));
        assertEquals(StatusExemplar.EMPRESTADO, exemplar.getStatus());

        verify(exemplarRepository).saveAll(Set.of(exemplar));
        verify(repository).save(any(Emprestimo.class));
    }
    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontradoAoCriar() {
        EmprestimoRequestDTO dto = criarDto(99L, Set.of(1L), LocalDateTime.now().plusDays(14));

        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.criar(dto)
        );

        assertEquals("Usuário não encontrado com id: 99", exception.getMessage());
        verifyNoInteractions(exemplarRepository);
        verify(repository, never()).save(any());
    }
    @Test
    void deveLancarExcecaoQuandoDataPrevistaNoPassado() {
        Usuario usuario = criarUsuario(1L);
        EmprestimoRequestDTO dto = criarDto(1L, Set.of(1L), LocalDateTime.now().minusDays(1));

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> service.criar(dto)
        );

        assertEquals("Data Inválida, tente novamnete", exception.getMessage());
        verifyNoInteractions(exemplarRepository);
        verify(repository, never()).save(any());
    }

}
