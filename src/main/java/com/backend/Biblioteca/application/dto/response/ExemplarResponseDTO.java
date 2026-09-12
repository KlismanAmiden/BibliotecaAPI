package com.backend.Biblioteca.application.dto.response;

import com.backend.Biblioteca.domain.enums.StatusExemplar;

public record ExemplarResponseDTO(
        Long id,
        Long livroId,
        StatusExemplar status
) {
}
