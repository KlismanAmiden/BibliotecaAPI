package com.backend.Biblioteca.infrastructure.repository;

import com.backend.Biblioteca.domain.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LivroRepository extends JpaRepository<Livro, Long> {
}
