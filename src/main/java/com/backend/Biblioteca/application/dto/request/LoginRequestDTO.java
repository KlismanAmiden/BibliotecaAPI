package com.backend.Biblioteca.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(

        @NotBlank(message = "Email é obrigatorio")
        @Email
        String email,
        @NotBlank(message = "Senha é obrigatorio")
        String senha

) {
}
