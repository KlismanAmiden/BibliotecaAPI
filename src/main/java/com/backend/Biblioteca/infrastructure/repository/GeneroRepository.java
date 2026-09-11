package com.backend.Biblioteca.infrastructure.repository;

import com.backend.Biblioteca.domain.model.Genero;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GeneroRepository extends JpaRepository<Genero, Long> {
    Optional<Genero> findByNome(String nome);
    boolean existsByNome(String nome);

}
