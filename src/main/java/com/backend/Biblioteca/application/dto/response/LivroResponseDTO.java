package com.backend.Biblioteca.application.dto.response;

import java.util.Set;

public record LivroResponseDTO(
        Long id,
        String titulo,
        String isbn,
        Integer anoPublicado,
        String descricao,
        String editora,
        Set<AutorResponseDTO> autores

) {
}
