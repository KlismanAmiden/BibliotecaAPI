package com.backend.Biblioteca.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "Autores")
@Getter
@Setter
public class Autor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false,columnDefinition = "TEXT")
    private String biografia;


    @Column(nullable = false)
    private Integer anoNascimento;

    @Column(nullable = false)
    private String nacionalidade;

    @ManyToMany(mappedBy = "autores")
    private Set<Livro> livros;
}
