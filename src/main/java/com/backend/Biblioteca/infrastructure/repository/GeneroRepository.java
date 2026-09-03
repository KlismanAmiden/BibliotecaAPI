package com.backend.Biblioteca.infrastructure.repository;

import com.backend.Biblioteca.domain.model.Genero;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GeneroRepository extends JpaRepository<Genero, Long> {

}
