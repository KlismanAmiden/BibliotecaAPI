package com.backend.Biblioteca.application.service;

import com.backend.Biblioteca.application.dto.response.AutorResponseDTO;
import com.backend.Biblioteca.domain.model.Autor;
import com.backend.Biblioteca.infrastructure.repository.AutorRepository;
import com.backend.Biblioteca.web.exception.ResourceNotFoundException;
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
public class AutorServiceTest {

    @Mock
    private AutorRepository repository;

    @InjectMocks
    private AutorService service;

    private Autor criarAutor(Long id, String nome, String biografia, Integer anoNascimento, String nacionalidade) {
        Autor autor = new Autor();
        autor.setId(id);
        autor.setNome(nome);
        autor.setBiografia(biografia);
        autor.setAnoNascimento(anoNascimento);
        autor.setNacionalidade(nacionalidade);
        return autor;
    }
    @Test
    void deveListarAutoresComSucesso() {
        Autor autor1 = criarAutor(1L, "Isaac Asimov", "Escritor de ficção científica", 1920, "Americana");
        Autor autor2 = criarAutor(2L, "Machado de Assis", "Escritor brasileiro", 1839, "Brasileira");

        when(repository.findAll()).thenReturn(List.of(autor1, autor2));

        List<AutorResponseDTO> resultado = service.listarTodos();

        assertEquals(2, resultado.size());
        assertEquals("Isaac Asimov", resultado.get(0).nome());
        assertEquals("Machado de Assis", resultado.get(1).nome());

        verify(repository).findAll();
    }
    @Test
    void deveRetornarListaVaziaQuandoNaoHaAutores() {
        when(repository.findAll()).thenReturn(List.of());

        List<AutorResponseDTO> resultado = service.listarTodos();

        assertTrue(resultado.isEmpty());
        verify(repository).findAll();
    }
    @Test
    void deveListarAutorPorIdComSucesso() {
        Autor autor = criarAutor(1L, "Isaac Asimov", "Escritor de ficção científica", 1920, "Americana");

        when(repository.findById(1L)).thenReturn(Optional.of(autor));

        AutorResponseDTO resultado = service.listarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
        assertEquals("Isaac Asimov", resultado.nome());
        assertEquals("Escritor de ficção científica", resultado.biografia());
        assertEquals(1920, resultado.anoNascimento());
        assertEquals("Americana", resultado.nacionalidade());

        verify(repository).findById(1L);
    }
    @Test
    void deveLancarExcecaoQuandoAutorNaoEncontradoPorId() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.listarPorId(99L)
        );

        assertEquals("Autor não encontrado com id: 99", exception.getMessage());
        verify(repository).findById(99L);
    }
}
