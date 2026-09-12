package com.backend.Biblioteca.infrastructure.repository;

import com.backend.Biblioteca.domain.model.Exemplar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExemplarRepository extends JpaRepository<Exemplar,Long> {
    List<Exemplar> findByLivroId(Long livroId);
}
