package com.backend.Biblioteca.domain.model;

import com.backend.Biblioteca.domain.enums.StatusEmprestimo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "emprestimos")
@Getter
@Setter
public class Emprestimo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuarioId", nullable = false)
    private Usuario usuario;

    @ManyToMany
    @JoinTable(
            name = ("emprestimo-exemplar"),
            joinColumns = @JoinColumn(name = "emprestimoID"),
            inverseJoinColumns = @JoinColumn( name = "exemplarId")
    )
    private Set<Exemplar> exemplares;

    @Column(nullable = false)
    private LocalDateTime dataEmprestimo;

    @Column(nullable = false)
    private LocalDateTime dataPrevistaDevolucao;

    private LocalDateTime dataDevolucao;

    @Enumerated(EnumType.STRING)
    @Column(name = "status",nullable = false)
    private StatusEmprestimo status =StatusEmprestimo.ATIVO;

    private BigDecimal multa = BigDecimal.ZERO;
}
