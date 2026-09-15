package com.backend.Biblioteca.application.dto.response;

import com.backend.Biblioteca.domain.enums.Role;

import java.time.LocalDateTime;

public record UsuarioResponseDTO(

        Long id,
        String nome,
        String email,
        String telefone,
        LocalDateTime dataCadastro,
        boolean ativo,
        Role role
) {


}
