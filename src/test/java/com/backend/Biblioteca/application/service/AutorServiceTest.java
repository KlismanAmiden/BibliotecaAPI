package com.backend.Biblioteca.application.service;

import com.backend.Biblioteca.domain.model.Autor;
import com.backend.Biblioteca.infrastructure.repository.AutorRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
}
