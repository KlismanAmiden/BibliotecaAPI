package com.backend.Biblioteca.application.service;

import com.backend.Biblioteca.application.dto.request.LivroRequestDTO;
import com.backend.Biblioteca.application.dto.response.LivroResponseDTO;
import com.backend.Biblioteca.domain.model.Autor;
import com.backend.Biblioteca.domain.model.Genero;
import com.backend.Biblioteca.domain.model.Livro;
import com.backend.Biblioteca.infrastructure.repository.AutorRepository;
import com.backend.Biblioteca.infrastructure.repository.GeneroRepository;
import com.backend.Biblioteca.infrastructure.repository.LivroRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LivroServiceTest {

    @Mock
    private LivroRepository repository;

    @Mock
    private AutorRepository autorRepository;

    @Mock
    private GeneroRepository generoRepository;

    @InjectMocks
    private LivroService service;

    private Autor criarAutor(Long id, String nome) {
        Autor autor = new Autor();
        autor.setId(id);
        autor.setNome(nome);
        autor.setBiografia("Biografia de " + nome);
        autor.setAnoNascimento(1970);
        autor.setNacionalidade("Brasileira");
        return autor;
    }

    private Genero criarGenero(Long id, String nome) {
        Genero genero = new Genero();
        genero.setId(id);
        genero.setNome(nome);
        genero.setDescricao("Descrição de " + nome);
        return genero;
    }
    private Livro criarLivro(Long id, String titulo, String isbn, Autor autor, Genero genero) {
        Livro livro = new Livro();
        livro.setId(id);
        livro.setTitulo(titulo);
        livro.setIsbn(isbn);
        livro.setAnoPublicado(1951);
        livro.setDescricao("Descrição de " + titulo);
        livro.setEditora("Aleph");
        livro.setAutores(Set.of(autor));
        livro.setGeneros(Set.of(genero));
        return livro;
    }

    private LivroRequestDTO criarDto(String isbn, Set<Long> autoresIds, Set<Long> generosIds) {
        return new LivroRequestDTO(
                "Fundação", isbn, 1951, "Primeiro livro da série Fundação",
                "Aleph", autoresIds, generosIds
        );
    }

    @Test
    void deveListarLivroComSucesso() {
        Autor autor = criarAutor(1L, "Isaac Asimov");
        Genero genero = criarGenero(1L, "Ficção Científica");
        Livro livro = criarLivro(1L, "Fundação", "978-0553293357", autor, genero);

        when(repository.findAll()).thenReturn(List.of(livro));
        List<LivroResponseDTO> resultado = service.listarTodos();

        assertEquals(1, resultado.size());
        assertEquals("Fundação",resultado.get(0).titulo());
        verify(repository).findAll();
    }
    @Test
    void deveListarLivroPorAutor(){
        Autor autor = criarAutor(1L, "Isaac Asimov");
        Genero genero = criarGenero(1L, "Ficção Científica");
        Livro livro = criarLivro(1L, "Fundação", "978-0553293357", autor, genero);

        when(repository.findByAutoresId(autor.getId())).thenReturn(List.of(livro));
        List<LivroResponseDTO> resultado = service.listarPorAutor(autor.getId());

        assertEquals(1, resultado.size());
        assertEquals("Fundação",resultado.get(0).titulo());
        verify(repository).findByAutoresId(autor.getId());
    }

}
