package com.backend.Biblioteca.web.controller;

import com.backend.Biblioteca.application.dto.request.LoginRequestDTO;
import com.backend.Biblioteca.application.dto.response.LoginResponseDTO;
import com.backend.Biblioteca.application.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Login e emissão de token JWT")
public class AuthController {

    private final AuthService service;

    @PostMapping("/login")
    @Operation(summary = "Autentica um usuário e retorna o token JWT", description = "Endpoint público.")
    public LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO dto) {
        return service.login(dto);
    }
}