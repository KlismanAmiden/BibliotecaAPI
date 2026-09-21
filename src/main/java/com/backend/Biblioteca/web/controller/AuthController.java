package com.backend.Biblioteca.web.controller;

import com.backend.Biblioteca.application.dto.request.LoginRequestDTO;
import com.backend.Biblioteca.application.dto.response.LoginResponseDTO;
import com.backend.Biblioteca.application.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/login")
    public LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO dto) {
        return service.login(dto);
    }
}