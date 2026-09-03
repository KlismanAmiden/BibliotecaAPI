package com.backend.Biblioteca.domain.model;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "Autores")
public class Autor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false,columnDefinition = "TEXT")
    private String biografia;


    @Column(nullable = false)
    private Integer dataNascimento;

    @Column(nullable = false)
    private String nacionalidade;

    @ManyToMany
    @JoinTable(
    name = "autor_livro",
    joinColumns = @JoinColumn(name = "autor_id"),
    inverseJoinColumns = @JoinColumn(name = "livro_id")
    )
    private Set<Livro> livros;
}
