package com.backend.Biblioteca.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record GeneroRequestDTO(

        @NotBlank(message = "Nome é obrigatorio")
        String nome,

        @NotBlank(message = "Descrição é obrigatorio")
        String descricao


) {
}
