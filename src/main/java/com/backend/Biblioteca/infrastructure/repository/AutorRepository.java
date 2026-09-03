package com.backend.Biblioteca.infrastructure.repository;

import com.backend.Biblioteca.domain.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AutorRepository extends JpaRepository<Autor, Long> {

}
