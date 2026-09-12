package com.backend.Biblioteca.application.service;

import com.backend.Biblioteca.domain.enums.StatusExemplar;
import com.backend.Biblioteca.domain.model.Exemplar;
import com.backend.Biblioteca.domain.model.Livro;
import com.backend.Biblioteca.infrastructure.repository.ExemplarRepository;
import com.backend.Biblioteca.infrastructure.repository.LivroRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ExemplarServiceTest {

    @Mock
    private ExemplarRepository repository;

    @Mock
    private LivroRepository livroRepository;

    @InjectMocks
    private ExemplarService service;

    private Livro criarLivro(Long id, String titulo) {
        Livro livro = new Livro();
        livro.setId(id);
        livro.setTitulo(titulo);
        return livro;
    }

    private Exemplar criarExemplar(Long id, Livro livro, StatusExemplar status) {
        Exemplar exemplar = new Exemplar();
        exemplar.setId(id);
        exemplar.setLivro(livro);
        exemplar.setStatus(status);
        return exemplar;
    }
}
