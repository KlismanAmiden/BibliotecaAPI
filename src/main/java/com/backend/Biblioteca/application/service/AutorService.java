package com.backend.Biblioteca.application.service;

import com.backend.Biblioteca.application.dto.request.AutorRequestDTO;
import com.backend.Biblioteca.application.dto.response.AutorResponseDTO;
import com.backend.Biblioteca.domain.model.Autor;
import com.backend.Biblioteca.infrastructure.repository.AutorRepository;
import com.backend.Biblioteca.web.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AutorService {

    private final AutorRepository repository;

    public List<AutorResponseDTO> listarTodos(){
        return repository.findAll().stream().map(this::toDTO).toList();
    }

    public AutorResponseDTO criar(AutorRequestDTO dto){

        Autor autor = new Autor();
        autor.setNome(dto.nome());
        autor.setBiografia(dto.biografia());
        autor.setAnoNascimento(dto.anoNascimento());
        autor.setNacionalidade(dto.nacionalidade());
        Autor salvo = repository.save(autor);
        return toDTO(salvo);
    }

    public AutorResponseDTO listarPorId(Long id){
        Autor autor = buscarPorEntidade(id);
        return toDTO(autor);
    }

    public AutorResponseDTO atualizar(Long id, AutorRequestDTO dto){
        Autor autor = buscarPorEntidade(id);

        autor.setNome(dto.nome());
        autor.setBiografia(dto.biografia());
        autor.setAnoNascimento(dto.anoNascimento());
        autor.setNacionalidade(dto.nacionalidade());

        Autor salvo = repository.save(autor);
        return toDTO(salvo);
    }

    public void deletar(Long id){
        Autor autor = buscarPorEntidade(id);
        repository.delete(autor);
    }

    //-------------------------------Métodos auxiliares privados-----------------------------------------

    private Autor buscarPorEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Autor não encontrado com id: " + id));
    }

    private AutorResponseDTO toDTO(Autor a){
        return new AutorResponseDTO(
                a.getId(),
                a.getNome(),
                a.getBiografia(),
                a.getAnoNascimento(),
                a.getNacionalidade()
        );
    }

}

