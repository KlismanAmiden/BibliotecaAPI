package com.backend.Biblioteca.application.service;

import com.backend.Biblioteca.application.dto.response.GeneroResponseDTO;
import com.backend.Biblioteca.domain.model.Genero;
import com.backend.Biblioteca.infrastructure.repository.GeneroRepository;
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
public class GeneroServiceTest {

    @Mock
    private GeneroRepository repository;

    @InjectMocks
    private GeneroService service;

    private Genero criarGenero(Long id,String nome,String descricao){
        Genero genero = new Genero();
        genero.setId(id);
        genero.setNome(nome);
        genero.setDescricao(descricao);
        return genero;
    }
    @Test
    void deveListarGenerosComSucesso() {
        Genero genero1 = criarGenero(1L, "Ficção Científica", "Livros de ficção científica");
        Genero genero2 = criarGenero(2L, "Fantasia", "Livros de fantasia");

        when(repository.findAll()).thenReturn(List.of(genero1, genero2));

        List<GeneroResponseDTO> resultado = service.listarTodos();

        assertEquals(2, resultado.size());
        assertEquals("Ficção Científica", resultado.get(0).nome());
        assertEquals("Fantasia", resultado.get(1).nome());

        verify(repository).findAll();
    }
    @Test
    void deveRetornarListaVaziaQuandoNaoHaGeneros() {
        when(repository.findAll()).thenReturn(List.of());

        List<GeneroResponseDTO> resultado = service.listarTodos();

        assertTrue(resultado.isEmpty());
        verify(repository).findAll();
    }

}
