package com.backend.Biblioteca.application.service;

import com.backend.Biblioteca.application.dto.response.ExemplarResponseDTO;
import com.backend.Biblioteca.domain.enums.StatusExemplar;
import com.backend.Biblioteca.domain.model.Exemplar;
import com.backend.Biblioteca.domain.model.Livro;
import com.backend.Biblioteca.infrastructure.repository.ExemplarRepository;
import com.backend.Biblioteca.infrastructure.repository.LivroRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

}
