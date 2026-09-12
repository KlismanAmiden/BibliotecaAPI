package com.backend.Biblioteca.application.service;

import com.backend.Biblioteca.application.dto.request.ExemplarRequestDTO;
import com.backend.Biblioteca.application.dto.response.ExemplarResponseDTO;
import com.backend.Biblioteca.domain.enums.StatusExemplar;
import com.backend.Biblioteca.domain.model.Exemplar;
import com.backend.Biblioteca.domain.model.Livro;
import com.backend.Biblioteca.infrastructure.repository.ExemplarRepository;
import com.backend.Biblioteca.infrastructure.repository.LivroRepository;
import com.backend.Biblioteca.web.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExemplarService {

    private final ExemplarRepository repository;

    private final LivroRepository livroRepository;

    public List<ExemplarResponseDTO> listarTodos(){
        return repository.findAll().stream().map(this::toDTO).toList();
    }
    public ExemplarResponseDTO listarPorId(Long id){
        Exemplar exemplar = buscarPorEntidade(id);
        return toDTO(exemplar);
    }
    public List<ExemplarResponseDTO> listarPorLIvro(Long livroId){
        return repository.findByLivroId(livroId).stream().map(this::toDTO).toList();
    }
    public ExemplarResponseDTO criar(ExemplarRequestDTO dto){
        Livro livro = buscarLivro(dto.livro_id());

        Exemplar exemplar = new Exemplar();
        exemplar.setLivro(livro);
        exemplar.setStatus(StatusExemplar.DISPONIVEL);
        Exemplar salvo = repository.save(exemplar);
        return toDTO(salvo);
    }
    public ExemplarResponseDTO atualizarStatus(Long id, StatusExemplar status) {
        Exemplar exemplar = buscarPorEntidade(id);
        exemplar.setStatus(status);
        return toDTO(repository.save(exemplar));
    }
    public void deletar(Long id){
        Exemplar exemplar = buscarPorEntidade(id);
        repository.delete(exemplar);
    }

    //-------------------------------Métodos auxiliares privados-----------------------------------------

    private Exemplar buscarPorEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exemplar não encontrado com id: " + id));
    }
    private Livro buscarLivro(Long id){
        return livroRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Livro não encontrado com id: "+id));
    }

    private ExemplarResponseDTO toDTO(Exemplar e){
        return new ExemplarResponseDTO(
                e.getId(),
                e.getLivro().getId(),
                e.getStatus()
        );
    }
}