package com.backend.Biblioteca.application.service;

import com.backend.Biblioteca.application.dto.request.GeneroRequestDTO;
import com.backend.Biblioteca.application.dto.response.GeneroResponseDTO;
import com.backend.Biblioteca.domain.model.Genero;
import com.backend.Biblioteca.infrastructure.repository.GeneroRepository;
import com.backend.Biblioteca.web.exception.BadRequestException;
import com.backend.Biblioteca.web.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GeneroService {

    private final GeneroRepository repository;

    public List<GeneroResponseDTO> listarTodos(){
        return repository.findAll().stream().map(this::toDTO).toList();
    }

    public GeneroResponseDTO listarPorId(Long id){
        Genero genero = buscarPorEntidade(id);
        return toDTO(genero);
    }

    public GeneroResponseDTO criar(GeneroRequestDTO dto){
        if (repository.existsByNome(dto.nome())) {
            throw new BadRequestException("genero já cadastrado com Nome: " + dto.nome());
        }

        Genero genero = new Genero();
        genero.setNome(dto.nome());
        genero.setDescricao(dto.descricao());
        Genero salvo = repository.save(genero);
        return toDTO(salvo);
    }

    public GeneroResponseDTO atualizar(Long id, GeneroRequestDTO dto){
        Genero genero = buscarPorEntidade(id);

        if (!genero.getNome().equals(dto.nome()) && repository.existsByNome(dto.nome())) {
            throw new BadRequestException("Já existe um genero cadastrado com o Nome: " + dto.nome());
        }

        genero.setNome(dto.nome());
        genero.setDescricao(dto.descricao());

        Genero salvo = repository.save(genero);
        return toDTO(salvo);
    }

    public void deletar(Long id){
        Genero genero = buscarPorEntidade(id);
        repository.delete(genero);
    }

    //-------------------------------Métodos auxiliares privados-----------------------------------------

    private Genero buscarPorEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Genero não encontrado com id: " + id));
    }

    private GeneroResponseDTO toDTO(Genero g){
        return new GeneroResponseDTO(
                g.getId(),
                g.getNome(),
                g.getDescricao()
        );}
}
