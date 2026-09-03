package com.backend.Biblioteca.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LivroRequestDTO(
        @NotBlank(message = "Titulo é obrigatorio")
        String ttulo,

        @NotBlank(message = "ISBN é obrigatorio")
        String isbn,

        @NotBlank(message = "Ano é obrigatorio")
        @Size(min = 4, max = 4, message = "Ano deve ter 4 digitos")
        Integer anoPublicado,

        @NotBlank(message = "descrição é obrigatorio")
        String descriao,

        @NotBlank(message = "Editora é obrigatorio")
        String editora

) {

}

