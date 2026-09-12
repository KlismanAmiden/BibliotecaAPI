package com.backend.Biblioteca.application.service;

import com.backend.Biblioteca.application.dto.request.ExemplarRequestDTO;
import com.backend.Biblioteca.application.dto.response.ExemplarResponseDTO;
import com.backend.Biblioteca.domain.enums.StatusExemplar;
import com.backend.Biblioteca.domain.model.Exemplar;
import com.backend.Biblioteca.domain.model.Livro;
import com.backend.Biblioteca.infrastructure.repository.ExemplarRepository;
import com.backend.Biblioteca.infrastructure.repository.LivroRepository;
import com.backend.Biblioteca.web.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExemplarServiceTest {

    @Mock
    private ExemplarRepository repository;

    @Mock
    private LivroRepository livroRepository;

    @InjectMocks
    private ExemplarService service;

    private Livro criarLivro(Long id, String titulo) {
        Livro livro = new Livro();
        livro.setId(id);
        livro.setTitulo(titulo);
        return livro;
    }

    private Exemplar criarExemplar(Long id, Livro livro, StatusExemplar status) {
        Exemplar exemplar = new Exemplar();
        exemplar.setId(id);
        exemplar.setLivro(livro);
        exemplar.setStatus(status);
        return exemplar;
    }
    @Test
    void deveListarExemplaresComSucesso() {
        Livro livro = criarLivro(1L, "Fundação");
        Exemplar exemplar1 = criarExemplar(1L, livro, StatusExemplar.DISPONIVEL);
        Exemplar exemplar2 = criarExemplar(2L, livro, StatusExemplar.EMPRESTADO);

        when(repository.findAll()).thenReturn(List.of(exemplar1, exemplar2));

        List<ExemplarResponseDTO> resultado = service.listarTodos();

        assertEquals(2, resultado.size());
        assertEquals(StatusExemplar.DISPONIVEL, resultado.get(0).status());
        assertEquals(StatusExemplar.EMPRESTADO, resultado.get(1).status());

        verify(repository).findAll();
    }
    @Test
    void deveRetornarListaVaziaQuandoNaoHaExemplares() {
        when(repository.findAll()).thenReturn(List.of());

        List<ExemplarResponseDTO> resultado = service.listarTodos();

        assertTrue(resultado.isEmpty());
        verify(repository).findAll();
    }

    @Test
    void deveListarExemplarPorIdComSucesso() {
        Livro livro = criarLivro(1L, "Fundação");
        Exemplar exemplar = criarExemplar(1L, livro, StatusExemplar.DISPONIVEL);

        when(repository.findById(1L)).thenReturn(Optional.of(exemplar));

        ExemplarResponseDTO resultado = service.listarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
        assertEquals(1L, resultado.livroId());
        assertEquals(StatusExemplar.DISPONIVEL, resultado.status());

        verify(repository).findById(1L);
    }

    @Test
    void deveLancarExcecaoQuandoExemplarNaoEncontradoPorId() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.listarPorId(99L)
        );

        assertEquals("Exemplar não encontrado com id: 99", exception.getMessage());
        verify(repository).findById(99L);
    }
    @Test
    void deveListarExemplaresPorLivroComSucesso() {
        Livro livro = criarLivro(1L, "Fundação");
        Exemplar exemplar1 = criarExemplar(1L, livro, StatusExemplar.DISPONIVEL);
        Exemplar exemplar2 = criarExemplar(2L, livro, StatusExemplar.INDISPONIVEL);

        when(repository.findByLivroId(1L)).thenReturn(List.of(exemplar1, exemplar2));

        List<ExemplarResponseDTO> resultado = service.listarPorLivro(1L);

        assertEquals(2, resultado.size());
        verify(repository).findByLivroId(1L);
    }
    @Test
    void deveCriarExemplarComSucesso() {
        Livro livro = criarLivro(1L, "Fundação");
        ExemplarRequestDTO dto = new ExemplarRequestDTO(1L);

        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro));
        when(repository.save(any(Exemplar.class))).thenAnswer(invocation -> {
            Exemplar exemplarSalvo = invocation.getArgument(0);
            exemplarSalvo.setId(10L);
            return exemplarSalvo;
        });

        ExemplarResponseDTO resultado = service.criar(dto);

        assertNotNull(resultado);
        assertEquals(10L, resultado.id());
        assertEquals(1L, resultado.livroId());
        assertEquals(StatusExemplar.DISPONIVEL, resultado.status());

        verify(livroRepository).findById(1L);
        verify(repository).save(argThat(e ->
                e.getLivro().equals(livro) &&
                        e.getStatus() == StatusExemplar.DISPONIVEL
        ));
    }
    @Test
    void deveLancarExcecaoQuandoLivroNaoEncontradoAoCriar() {
        ExemplarRequestDTO dto = new ExemplarRequestDTO(99L);

        when(livroRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.criar(dto)
        );

        assertEquals("Livro não encontrado com id: 99", exception.getMessage());

        verify(livroRepository).findById(99L);
        verify(repository, never()).save(any());
    }


}
