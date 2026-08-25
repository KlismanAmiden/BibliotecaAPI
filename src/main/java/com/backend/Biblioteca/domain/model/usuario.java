package com.backend.Biblioteca.domain.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "usuarios")
@Getter
@Setter
public class usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name ="nome",nullable = false)
    private String nome;

    @Column(name ="nome",nullable = false,unique = true)
    private String email;

    @Column(name = "senha", nullable = false)
    private String senha;

    @Column(name = "telefone", nullable = false)
    private String telefone;

    @Column(name = "data_cadastro",nullable = false)
    private LocalDateTime data_cadastro;

    @Column(name = "ativo",nullable = false)
    private boolean ativo;

    

}