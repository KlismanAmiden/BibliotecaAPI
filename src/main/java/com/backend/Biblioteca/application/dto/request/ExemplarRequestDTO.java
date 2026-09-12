package com.backend.Biblioteca.application.dto.request;

import jakarta.validation.constraints.NotNull;

public record ExemplarRequestDTO(

        @NotNull
        Long livro_id

) {
}
