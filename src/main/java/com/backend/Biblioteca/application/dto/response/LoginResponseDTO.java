package com.backend.Biblioteca.application.dto.response;

public record LoginResponseDTO(

         String token,
         Long id,
         String nome,
         String email
) {
}
