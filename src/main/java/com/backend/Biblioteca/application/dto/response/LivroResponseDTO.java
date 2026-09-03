package com.backend.Biblioteca.application.dto.response;

public record LivroResponseDTO(
        Long id,
        String titulo,
        String isbn,
        Integer anoPublicado,
        String descricao,
        String editora
) {
}
