package com.backend.Biblioteca.infrastructure.repository;

import com.backend.Biblioteca.domain.model.usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface usuarioRepository extends JpaRepository<usuario, Long> {

    Optional<usuario> findByEmail(String email);
    boolean existsByEmail(String email);
}
