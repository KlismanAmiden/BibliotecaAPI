package com.backend.Biblioteca.application.service;

import com.backend.Biblioteca.domain.model.Genero;
import com.backend.Biblioteca.infrastructure.repository.GeneroRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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


}
