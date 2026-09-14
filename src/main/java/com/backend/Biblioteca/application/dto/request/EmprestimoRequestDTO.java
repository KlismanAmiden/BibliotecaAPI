package com.backend.Biblioteca.application.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Set;

public record EmprestimoRequestDTO(
        @NotNull
        Long usuarioId,

        @NotEmpty
        Set<Long> exemplaresIds,

        @NotNull
        @JsonFormat(pattern = "dd/MM/yyyy'T'HH:mm")
        LocalDateTime dataPrevistaDevolucao
) {
}
