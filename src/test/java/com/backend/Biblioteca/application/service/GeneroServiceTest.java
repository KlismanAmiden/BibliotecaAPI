package com.backend.Biblioteca.application.service;

import com.backend.Biblioteca.application.dto.request.GeneroRequestDTO;
import com.backend.Biblioteca.application.dto.response.GeneroResponseDTO;
import com.backend.Biblioteca.domain.model.Genero;
import com.backend.Biblioteca.infrastructure.repository.GeneroRepository;
import com.backend.Biblioteca.web.exception.BadRequestException;
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
    @Test
    void deveListarGeneroPorIdComSucesso() {
        Genero genero = criarGenero(1L, "Ficção Científica", "Livros de ficção científica");

        when(repository.findById(1L)).thenReturn(Optional.of(genero));

        GeneroResponseDTO resultado = service.listarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
        assertEquals("Ficção Científica", resultado.nome());
        assertEquals("Livros de ficção científica", resultado.descricao());

        verify(repository).findById(1L);
    }
    @Test
    void deveLancarExcecaoQuandoGeneroNaoEncontradoPorId() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.listarPorId(99L)
        );

        assertEquals("Genero não encontrado com id: 99", exception.getMessage());
        verify(repository).findById(99L);
    }
    @Test
    void deveCriarGeneroComSucesso() {
        GeneroRequestDTO dto = new GeneroRequestDTO("Ficção Científica", "Livros de ficção científica");

        when(repository.existsByNome(dto.nome())).thenReturn(false);
        when(repository.save(any(Genero.class))).thenAnswer(invocation -> {
            Genero generoSalvo = invocation.getArgument(0);
            generoSalvo.setId(1L);
            return generoSalvo;
        });

        GeneroResponseDTO resultado = service.criar(dto);

        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
        assertEquals("Ficção Científica", resultado.nome());
        assertEquals("Livros de ficção científica", resultado.descricao());

        verify(repository).existsByNome(dto.nome());
        verify(repository).save(argThat(g ->
                g.getNome().equals(dto.nome()) &&
                        g.getDescricao().equals(dto.descricao())
        ));
    }
    @Test
    void deveLancarExcecaoQuandoNomeJaExisteAoCriar() {
        GeneroRequestDTO dto = new GeneroRequestDTO("Ficção Científica", "Livros de ficção científica");

        when(repository.existsByNome(dto.nome())).thenReturn(true);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> service.criar(dto)
        );

        assertEquals("genero já cadastrado com Nome: Ficção Científica", exception.getMessage());

        verify(repository).existsByNome(dto.nome());
        verify(repository, never()).save(any());
    }
    @Test
    void deveAtualizarGeneroComSucesso() {
        Genero existente = criarGenero(1L, "Ficção Científica", "Descrição antiga");
        GeneroRequestDTO dto = new GeneroRequestDTO("Ficção Científica", "Descrição nova");

        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(any(Genero.class))).thenReturn(existente);

        GeneroResponseDTO resultado = service.atualizar(1L, dto);

        assertEquals("Descrição nova", resultado.descricao());

        verify(repository).findById(1L);
        verify(repository).save(existente);
        verify(repository, never()).existsByNome(any());
    }


}
