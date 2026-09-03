package com.backend.Biblioteca.infrastructure.repository;

import com.backend.Biblioteca.domain.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LivroRepository extends JpaRepository<Livro, Long> {
    List<Livro> findByAutoresId(Long autorId);
    List<Livro> findByGenerosId(Long generoId);

}
