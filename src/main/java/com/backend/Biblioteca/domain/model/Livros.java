package com.backend.Biblioteca.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Livros")
@Getter
@Setter
public class Livros {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String isbn;

    @Column(nullable = false)
    private int AnoPublicado;

    @Column(nullable = false,columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false)
    private String editora;
}
