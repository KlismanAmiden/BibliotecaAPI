package com.backend.Biblioteca.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record usuarioRequestDTO(

        @NotBlank(message = "Nome é obrigatorio")
        String nome,

        @NotBlank(message = "Email é obrigatorio")
        @Email
        String email,

        @NotBlank(message = "Senha é obrigatorio")
        @Size(min = 1, max = 100)
        String senha,

        @NotBlank(message = "Telefone é obrigatorio")
        String telefone

) {
}
