package com.backend.Biblioteca.infrastructure.repository;

import com.backend.Biblioteca.domain.enums.StatusEmprestimo;
import com.backend.Biblioteca.domain.model.Emprestimo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmprestimoRepository extends JpaRepository<Emprestimo,Long> {
    List<Emprestimo> findByUsuarioId(Long usuarioId);
    List<Emprestimo> findByUsuarioIdAndStatus(Long usuarioId, StatusEmprestimo status);
}
