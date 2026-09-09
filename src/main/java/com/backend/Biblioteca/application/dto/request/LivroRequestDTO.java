package com.backend.Biblioteca.application.dto.request;

import jakarta.validation.constraints.*;

import java.util.Set;


public record LivroRequestDTO(
        @NotBlank(message = "Titulo é obrigatorio")
        String titulo,

        @NotBlank(message = "ISBN é obrigatorio")
        String isbn,

        @Min(1000)
        @Max(2026)
        @NotNull
        Integer anoPublicado,

        @NotBlank(message = "descrição é obrigatorio")
        String descricao,

        @NotBlank(message = "Editora é obrigatorio")
        String editora,

        @NotEmpty(message = "Autores é obrigatorio")
        Set<Long>autoresIds,

        @NotEmpty(message = "Generos é obrigatorio")
        Set<Long>generosIds

) {

}

