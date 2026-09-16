package com.backend.Biblioteca.application.service;

import com.backend.Biblioteca.application.dto.request.LoginRequestDTO;
import com.backend.Biblioteca.application.dto.response.LoginResponseDTO;
import com.backend.Biblioteca.domain.model.Usuario;
import com.backend.Biblioteca.infrastructure.repository.UsuarioRepository;
import com.backend.Biblioteca.infrastructure.security.JwtUtil;
import com.backend.Biblioteca.web.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    public LoginResponseDTO login(LoginRequestDTO dto) {

        Usuario usuario = repository.findByEmail(dto.email())
                .orElseThrow(() ->
                        new BadRequestException("Email ou Senha inválidos"));

        if (encoder.matches(dto.senha(), usuario.getSenha())) {
            throw new BadRequestException("Email ou Senha inválidos");
        }

        String token = jwtUtil.generateToken(
                usuario.getEmail(),
                usuario.getId(),
                usuario.getRole().name()
        );

        return new LoginResponseDTO(
                token,
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail()
        );
    }
}
