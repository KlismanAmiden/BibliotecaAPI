package com.backend.Biblioteca.infrastructure.repository;

import com.backend.Biblioteca.domain.model.Emprestimo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmprestimoRepository extends JpaRepository<Emprestimo,Long> {
    Optional<Emprestimo> findByUsuarioId(Long usuarioId);
}
