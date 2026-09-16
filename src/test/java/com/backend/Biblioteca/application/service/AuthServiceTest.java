package com.backend.Biblioteca.application.service;

import com.backend.Biblioteca.application.dto.request.LoginRequestDTO;
import com.backend.Biblioteca.application.dto.response.LoginResponseDTO;
import com.backend.Biblioteca.domain.enums.Role;
import com.backend.Biblioteca.domain.model.Usuario;
import com.backend.Biblioteca.infrastructure.repository.UsuarioRepository;
import com.backend.Biblioteca.infrastructure.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService
            service;
    private Usuario criarUsuario(Long id, String email, String senhaHash, Role role) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNome("Kl");
        usuario.setEmail(email);
        usuario.setSenha(senhaHash);
        usuario.setTelefone("71999999999");
        usuario.setDataCadastro(LocalDateTime.now());
        usuario.setAtivo(true);
        usuario.setRole(role);
        return usuario;
    }
    @Test
    void deveLogarComSucessoQuandoCredenciaisValidas() {
        Usuario usuario = criarUsuario(1L, "kl@teste.com", "hashSalvo", Role.USUARIO);
        LoginRequestDTO dto = new LoginRequestDTO("kl@teste.com", "senha123");

        when(repository.findByEmail("kl@teste.com")).thenReturn(Optional.of(usuario));
        when(encoder.matches("senha123", "hashSalvo")).thenReturn(true);
        when(jwtUtil.generateToken("kl@teste.com", 1L, "USUARIO")).thenReturn("token-jwt-fake");

        LoginResponseDTO response = service.login(dto);

        assertEquals("token-jwt-fake", response.token());
        assertEquals(1L, response.id());
        assertEquals("Kl", response.nome());
        assertEquals("kl@teste.com", response.email());

        verify(jwtUtil).generateToken("kl@teste.com", 1L, "USUARIO");
    }
}
