package com.backend.Biblioteca.application.service;

import com.backend.Biblioteca.domain.enums.Role;
import com.backend.Biblioteca.domain.model.Usuario;
import com.backend.Biblioteca.infrastructure.repository.UsuarioRepository;
import com.backend.Biblioteca.infrastructure.security.JwtUtil;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

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
}
