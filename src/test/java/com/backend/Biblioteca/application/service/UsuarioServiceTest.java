package com.backend.Biblioteca.application.service;

import com.backend.Biblioteca.application.dto.request.UsuarioRequestDTO;
import com.backend.Biblioteca.application.dto.response.UsuarioResponseDTO;
import com.backend.Biblioteca.domain.model.Usuario;
import com.backend.Biblioteca.infrastructure.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void deveCriarUsuarioComSucesso() {

        UsuarioRequestDTO dto = new UsuarioRequestDTO("Klisman", "klisman@email.com", "123456", "71999999999");

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setTelefone(dto.telefone());
        usuario.setSenha("senha-criptografada");


        when(repository.existsByEmail(dto.email()))
                .thenReturn(false);

        when(passwordEncoder.encode(dto.senha()))
                .thenReturn("senha-criptografada");

        when(repository.save(any(Usuario.class)))
                .thenReturn(usuario);

        UsuarioResponseDTO response = usuarioService.criar(dto);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Klisman", response.nome());
        assertEquals("klisman@email.com", response.email());
        assertEquals("71999999999", response.telefone());

        verify(repository).existsByEmail(dto.email());
        verify(passwordEncoder).encode(dto.senha());
        verify(repository).save(argThat(usuarioSalvo ->
                usuarioSalvo.getNome().equals(dto.nome()) &&
                        usuarioSalvo.getEmail().equals(dto.email()) &&
                        usuarioSalvo.getTelefone().equals(dto.telefone()) &&
                        usuarioSalvo.getSenha().equals("senha-criptografada") &&
                        usuarioSalvo.getDataCadastro() != null
        ));
    }
    @Test
    void deveLancarExcecaoQuandoEmailJaExistir() {

        UsuarioRequestDTO dto = new UsuarioRequestDTO("Klisman", "klisman@email.com", "123456", "71999999999");

        when(repository.existsByEmail(dto.email()))
                .thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> usuarioService.criar(dto));

        assertEquals("Email já cadastrado", exception.getMessage());

        verify(repository).existsByEmail(dto.email());
        verify(repository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }
    @Test
    void deveListarUsuariosComSucesso() {

        Usuario usuario1 = new Usuario();
        usuario1.setId(1L);
        usuario1.setNome("Klisman");
        usuario1.setEmail("klisman@email.com");
        usuario1.setTelefone("71999999999");

        Usuario usuario2 = new Usuario();
        usuario2.setId(2L);
        usuario2.setNome("João");
        usuario2.setEmail("joao@email.com");
        usuario2.setTelefone("71988888888");

        when(repository.findAll())
                .thenReturn(List.of(usuario1, usuario2));

        List<UsuarioResponseDTO> response = usuarioService.Listar();

        assertNotNull(response);
        assertEquals(2, response.size());

        assertEquals(1L, response.get(0).id());
        assertEquals("Klisman", response.get(0).nome());
        assertEquals("klisman@email.com", response.get(0).email());
        assertEquals("71999999999", response.get(0).telefone());

        assertEquals(2L, response.get(1).id());
        assertEquals("João", response.get(1).nome());
        assertEquals("joao@email.com", response.get(1).email());
        assertEquals("71988888888", response.get(1).telefone());

        verify(repository).findAll();
    }
}

