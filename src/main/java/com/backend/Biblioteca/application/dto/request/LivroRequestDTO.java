package com.backend.Biblioteca.application.dto.request;

import jakarta.validation.constraints.*;

import java.util.Set;


public record LivroRequestDTO(
        @NotBlank(message = "Titulo é obrigatorio")
        String ttulo,

        @NotBlank(message = "ISBN é obrigatorio")
        String isbn,

        @Min(1000)
        @Max(2026)
        @NotNull
        Integer anoPublicado,

        @NotBlank(message = "descrição é obrigatorio")
        String descriao,

        @NotBlank(message = "Editora é obrigatorio")
        String editora,

        @NotBlank(message = "Autores é obrigatorio")
        Set<Long>autoresIds,

        @NotBlank(message = "Generos é obrigatorio")
        Set<Long>generosIds

) {

}

