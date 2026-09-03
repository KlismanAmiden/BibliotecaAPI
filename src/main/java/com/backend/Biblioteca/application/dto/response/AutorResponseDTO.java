package com.backend.Biblioteca.application.dto.response;

import java.util.Set;

public record AutorResponseDTO(
        Long id,
        String nome,
        String biografia,
        Integer anoNascimento,
        String nacionalidade,
        Set<LivroResponseDTO> livros
) {
}
