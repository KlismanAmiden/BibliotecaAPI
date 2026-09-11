package com.backend.Biblioteca.application.service;

import com.backend.Biblioteca.application.dto.request.LivroRequestDTO;
import com.backend.Biblioteca.application.dto.response.LivroResponseDTO;
import com.backend.Biblioteca.domain.model.Autor;
import com.backend.Biblioteca.domain.model.Genero;
import com.backend.Biblioteca.domain.model.Livro;
import com.backend.Biblioteca.infrastructure.repository.AutorRepository;
import com.backend.Biblioteca.infrastructure.repository.GeneroRepository;
import com.backend.Biblioteca.infrastructure.repository.LivroRepository;
import com.backend.Biblioteca.web.exception.BadRequestException;
import com.backend.Biblioteca.web.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        List<LivroResponseDTO> resultado = service.listarPorAutor(1L);

        assertEquals(1, resultado.size());
        assertEquals("Fundação",resultado.get(0).titulo());
        verify(repository).findByAutoresId(autor.getId());
    }
    @Test
    void deveListarLivroPorGenero(){
        Autor autor = criarAutor(1L, "Isaac Asimov");
        Genero genero = criarGenero(1L, "Ficção Científica");
        Livro livro = criarLivro(1L, "Fundação", "978-0553293357", autor, genero);

        when(repository.findByGenerosId(genero.getId())).thenReturn(List.of(livro));
        List<LivroResponseDTO> resultado = service.listarPorGenero(1L);

        assertEquals(1, resultado.size());
        assertEquals("Fundação",resultado.get(0).titulo());
        verify(repository).findByGenerosId(genero.getId());
    }
    @Test
    void deveListarLivroPorId() {
        Autor autor = criarAutor(1L, "Isaac Asimov");
        Genero genero = criarGenero(1L, "Ficção Científica");
        Livro livro = criarLivro(1L, "Fundação", "978-0553293357", autor, genero);

        when(repository.findById(1L)).thenReturn(Optional.of(livro));

        LivroResponseDTO resultado = service.buscarPorId(1L);

        assertEquals("Fundação", resultado.titulo());
        verify(repository).findById(1L);
    }
    @Test
    void deveLancarExcecaoQuandoLivroNaoEncontrado(){
        when(repository.findById(99L)).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.buscarPorId(99L)
        );
        assertEquals("Livro não encontrado com id: 99", exception.getMessage());
    }
    @Test
    void deveCriarLivroComSucesso() {
        Autor autor1 = criarAutor(1L, "Isaac Asimov");
        Autor autor2 = criarAutor(2L, "Robert Silverberg");
        Genero genero = criarGenero(1L, "Ficção Científica");

        LivroRequestDTO dto = criarDto("978-0553293357", Set.of(1L, 2L), Set.of(1L));

        when(repository.existsByIsbn(dto.isbn())).thenReturn(false);
        when(autorRepository.findById(1L)).thenReturn(Optional.of(autor1));
        when(autorRepository.findById(2L)).thenReturn(Optional.of(autor2));
        when(generoRepository.findById(1L)).thenReturn(Optional.of(genero));
        when(repository.save(any(Livro.class))).thenAnswer(invocation -> {
            Livro livroSalvo = invocation.getArgument(0);
            livroSalvo.setId(10L);
            return livroSalvo;
        });

        LivroResponseDTO response = service.criar(dto);

        assertNotNull(response);
        assertEquals(10L,response.id());
        assertEquals(2,response.autores().size());
        assertEquals(1,response.generos().size());

        verify(repository).existsByIsbn(dto.isbn());
        verify(repository).save(any(Livro.class));
    }
    @Test
    void deveLancarExcecaoQuandoISBNJaExisteAoCriar(){
        LivroRequestDTO dto = criarDto("978-0553293357", Set.of(1L, 2L), Set.of(1L));

        when(repository.existsByIsbn(dto.isbn())).thenReturn(true);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> service.criar(dto));

        assertEquals("Livro já cadastrado com ISBN: 978-0553293357",exception.getMessage());
        verifyNoInteractions(autorRepository,generoRepository);
        verify(repository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoAutorNaoEncontradoAoCriar() {
        LivroRequestDTO dto = criarDto("978-0553293357", Set.of(99L), Set.of(1L));

        when(repository.existsByIsbn(dto.isbn())).thenReturn(false);
        when(autorRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.criar(dto)
        );

        assertEquals("Autor não encontrado com id: 99", exception.getMessage());
        verifyNoInteractions(generoRepository);
        verify(repository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoGeneroNaoEncontradoAoCriar() {
        Autor autor = criarAutor(1L, "Isaac Asimov");
        LivroRequestDTO dto = criarDto("978-0553293357", Set.of(1L), Set.of(99L));

        when(repository.existsByIsbn(dto.isbn())).thenReturn(false);
        when(autorRepository.findById(1L)).thenReturn(Optional.of(autor));
        when(generoRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.criar(dto)
        );

        assertEquals("Genero não encontrado com id: 99", exception.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void deveAtualizarLivroComSucesso(){
        Autor autor = criarAutor(1L, "Isaac Asimov");
        Genero genero = criarGenero(1L, "Ficção Científica");
        Livro existente = criarLivro(1L, "Fundação", "978-0553293357", autor, genero);

        LivroRequestDTO dto = criarDto("978-0553293357", Set.of(1L), Set.of(1L));

        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(autorRepository.findById(1L)).thenReturn(Optional.of(autor));
        when(generoRepository.findById(1L)).thenReturn(Optional.of(genero));
        when(repository.save(any(Livro.class))).thenReturn(existente);

        LivroResponseDTO response = service.atualizar(1L,dto);

        assertEquals("Fundação",response.titulo());
        verify(repository).findById(1L);
        verify(repository).save(existente);
        verify(repository, never()).existsByIsbn(any());
    }

    @Test
    void deveValidarISBNDuplicadoAoAtualizarParaOutroISBN(){
        Autor autor = criarAutor(1L, "Isaac Asimov");
        Genero genero = criarGenero(1L, "Ficção Científica");
        Livro existente = criarLivro(1L, "Fundação", "978-0553293357", autor, genero);

        LivroRequestDTO dto = criarDto("978-1111111111", Set.of(1L), Set.of(1L));

        when(repository.existsByIsbn("978-1111111111")).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(existente));


        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> service.atualizar(1L, dto));

        assertEquals("Já existe um livro cadastrado com o ISBN: 978-1111111111", exception.getMessage());
        verify(repository, never()).save(any());
    }
    @Test
    void deveLancarExcecaoQuandoLivroNaoEncontradoAoAtualizar(){
        LivroRequestDTO dto = criarDto("978-0553293357", Set.of(1L), Set.of(1L));

        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.atualizar(99L, dto));
        verify(repository, never()).save(any());
    }

    @Test
    void deveDeletarLivroComSucesso() {
        Autor autor = criarAutor(1L, "Isaac Asimov");
        Genero genero = criarGenero(1L, "Ficção Científica");
        Livro livro = criarLivro(1L, "Fundação", "978-0553293357", autor, genero);

        when(repository.findById(1L)).thenReturn(Optional.of(livro));

        service.deletar(1L);

        verify(repository).findById(1L);
        verify(repository).delete(livro);
    }
}
