package com.backend.Biblioteca.infrastructure.repository;

import com.backend.Biblioteca.domain.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LivroRepository extends JpaRepository<Livro, Long> {
    List<Livro> findByAutoresId(Long autorId);
    List<Livro> findByGenerosId(Long generoId);
    Optional<Livro> findByIsbn(String isbn);
    boolean existsByIsbn(String isbn);
}
