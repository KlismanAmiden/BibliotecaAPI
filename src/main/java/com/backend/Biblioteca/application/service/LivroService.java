package com.backend.Biblioteca.application.service;

import com.backend.Biblioteca.application.dto.request.LivroRequestDTO;
import com.backend.Biblioteca.application.dto.response.AutorResponseDTO;
import com.backend.Biblioteca.application.dto.response.GeneroResponseDTO;
import com.backend.Biblioteca.application.dto.response.LivroResponseDTO;
import com.backend.Biblioteca.domain.model.Autor;
import com.backend.Biblioteca.domain.model.Genero;
import com.backend.Biblioteca.domain.model.Livro;
import com.backend.Biblioteca.infrastructure.repository.AutorRepository;
import com.backend.Biblioteca.infrastructure.repository.GeneroRepository;
import com.backend.Biblioteca.infrastructure.repository.LivroRepository;
import com.backend.Biblioteca.web.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LivroService {

    private final LivroRepository repository;
    private final AutorRepository autorRepository;
    private final GeneroRepository generoRepository;

    public List<LivroResponseDTO> listarTodos(){
        return repository.findAll().stream().map(this::toDTO).toList();
    }

    public List<LivroResponseDTO> listarPorGenero(Long generoId){
        return repository.findByGenerosId(generoId).stream().map(this::toDTO).toList();
    }

    public List<LivroResponseDTO> listarPorAutor(Long autorId){
        return repository.findByAutoresId(autorId).stream().map(this::toDTO).toList();
    }

    public LivroResponseDTO buscarPorId(Long id){
        Livro livro = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Livro não encontrado com id: " + id));
        return toDTO(livro);
    }

    public LivroResponseDTO criar(LivroRequestDTO dto){

        Set<Autor> autores = dto.autoresIds().stream()
                .map(id -> autorRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Autor não encontrado com id: " + id)))
                .collect(Collectors.toSet());

        Set<Genero> generos = dto.generosIds().stream()
                .map(id -> generoRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Genero não encontrado com id: " + id)))
                .collect(Collectors.toSet());

        Livro livro = new Livro();
        livro.setTitulo(dto.titulo());
        livro.setIsbn(dto.isbn());
        livro.setAnoPublicado(dto.anoPublicado());
        livro.setDescricao(dto.descricao());
        livro.setEditora(dto.editora());
        livro.setAutores(autores);
        livro.setGeneros(generos);

        Livro salvo = repository.save(livro);
        return toDTO(salvo);
    }

    private LivroResponseDTO toDTO(Livro l) {
        Set<AutorResponseDTO> autoresDTO = l.getAutores().stream()
                .map(a -> new AutorResponseDTO(
                        a.getId(), a.getNome(), a.getBiografia(),
                        a.getAnoNascimento(), a.getNacionalidade(), Set.of()))
                .collect(Collectors.toSet());

        Set<GeneroResponseDTO> generosDTO = l.getGeneros().stream()
                .map(g -> new GeneroResponseDTO(g.getId(), g.getNome(), g.getDescricao()))
                .collect(Collectors.toSet());

        return new LivroResponseDTO(
                l.getId(),
                l.getTitulo(),
                l.getIsbn(),
                l.getAnoPublicado(),
                l.getDescricao(),
                l.getEditora(),
                autoresDTO,
                generosDTO
        );
    }
}

