package com.backend.Biblioteca.domain.model;

import jakarta.persistence.*;

import java.util.Set;

@Entity
public class Genero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false,columnDefinition = "TEXT")
    private String descricao;

    @ManyToMany
    @JoinTable(
        name = "genero_livro",
        joinColumns = @JoinColumn(name = "genero_id"),
        inverseJoinColumns = @JoinColumn(name = "livro_id")
    )
    private Set<Livro> livros;
}
