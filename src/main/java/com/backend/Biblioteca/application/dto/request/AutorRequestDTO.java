package com.backend.Biblioteca.application.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AutorRequestDTO(

        @NotBlank(message = "Nome é obrigatorio")
        String nome,

        String biografia,

        @Min(1000)
        @Max(2026)
        @NotNull
        Integer anoNascimento,

        @NotBlank(message = "Nacionalidade é obrigatorio")
        String nacionalidade
) {
}
