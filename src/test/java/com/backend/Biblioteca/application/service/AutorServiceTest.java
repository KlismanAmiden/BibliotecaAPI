package com.backend.Biblioteca.application.service;

import com.backend.Biblioteca.application.dto.response.AutorResponseDTO;
import com.backend.Biblioteca.domain.model.Autor;
import com.backend.Biblioteca.infrastructure.repository.AutorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
}
