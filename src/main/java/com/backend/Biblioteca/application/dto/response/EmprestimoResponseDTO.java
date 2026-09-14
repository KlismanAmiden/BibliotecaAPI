package com.backend.Biblioteca.application.dto.response;

import com.backend.Biblioteca.domain.enums.StatusEmprestimo;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public record EmprestimoResponseDTO(
        Long id,
        Long usuarioId,
        Set<Long> exemplaresIds,
        LocalDateTime dataEmprestimo,

        @JsonFormat(pattern = "dd/MM/yyyy'T'HH:mm:ss")
        LocalDateTime dataPrevistaDevolucao,

        @JsonFormat(pattern = "dd/MM/yyyy'T'HH:mm:ss")
        LocalDateTime dataDevolucao,

        StatusEmprestimo status,
        BigDecimal multa
) {
}
