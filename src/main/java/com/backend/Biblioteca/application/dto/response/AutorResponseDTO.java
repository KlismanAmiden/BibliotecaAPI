package com.backend.Biblioteca.application.dto.response;

public record AutorResponseDTO(
        Long id,
        String nome,
        String biografia,
        Integer anoNascimento,
        String nacionalidade

) {
}
