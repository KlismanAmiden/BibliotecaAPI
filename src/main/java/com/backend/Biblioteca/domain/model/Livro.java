package com.backend.Biblioteca.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "Livros")
@Getter
@Setter
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, unique = true)
    private String isbn;

    @Column(nullable = false)
    private int anoPublicado;

    @Column(nullable = false,columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false)
    private String editora;

    @OneToMany(mappedBy = "livro", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Exemplar> exemplares = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "livros_autores",
            joinColumns = @JoinColumn(name = "livroId"),
            inverseJoinColumns = @JoinColumn(name = "autorId")
    )
    private Set<Autor> autores;

    @ManyToMany
    @JoinTable(
            name = "livros_generos",
            joinColumns = @JoinColumn(name = "livroId"),
            inverseJoinColumns = @JoinColumn(name = "generoId")
    )
    private Set<Genero> generos;


}
