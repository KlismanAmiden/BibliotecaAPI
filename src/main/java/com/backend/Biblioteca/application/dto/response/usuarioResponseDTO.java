package com.backend.Biblioteca.application.dto.response;

import java.time.LocalDateTime;

public record usuarioResponseDTO(

        Long id,
        String nombre,
        String email,
        String telefone,
        LocalDateTime dataCadastro,
        boolean ativo
) {


}
