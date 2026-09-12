package com.backend.Biblioteca.domain.model;

import com.backend.Biblioteca.domain.enums.StatusExemplar;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Exemplares")
@Getter
@Setter
public class Exemplar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "livroId",nullable = false)
    private Livro livro;

    @Enumerated(EnumType.STRING)
    @Column(name = "status",nullable = false)
    private StatusExemplar status = StatusExemplar.DISPONIVEL;



}
