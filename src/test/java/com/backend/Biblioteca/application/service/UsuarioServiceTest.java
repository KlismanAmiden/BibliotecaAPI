package com.backend.Biblioteca.application.service;

import com.backend.Biblioteca.application.dto.request.UsuarioRequestDTO;
import com.backend.Biblioteca.application.dto.response.UsuarioResponseDTO;
import com.backend.Biblioteca.domain.enums.Role;
import com.backend.Biblioteca.domain.model.Usuario;
import com.backend.Biblioteca.infrastructure.repository.UsuarioRepository;
import com.backend.Biblioteca.infrastructure.security.AuthenticatedUser;
import com.backend.Biblioteca.web.exception.BadRequestException;
import com.backend.Biblioteca.web.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticatedUser authenticatedUser;

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

       BadRequestException exception = assertThrows(BadRequestException.class, () -> usuarioService.criar(dto));

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

        List<UsuarioResponseDTO> response = usuarioService.ListarTodos();

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

    @Test void deveListarUsuarioPorIdQuandoForProprioUsuario() {

        Usuario usuario = new Usuario();
        usuario.setId(1L); usuario.setNome("Klisman");
        usuario.setEmail("klisman@email.com");
        usuario.setTelefone("71999999999");
        usuario.setRole(Role.USUARIO);

        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(authenticatedUser.getEmail()).thenReturn("klisman@email.com");
        when(authenticatedUser.isAdmin()).thenReturn(false);

        UsuarioResponseDTO response = usuarioService.listarPorId(1L);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Klisman", response.nome());
        assertEquals("klisman@email.com", response.email());
        assertEquals(Role.USUARIO, response.role());

        verify(repository).findById(1L);
        verify(authenticatedUser).getEmail();
        verify(authenticatedUser).isAdmin();
    }
    @Test void deveListarUsuarioPorIdQuandoForAdmin() {

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Klisman");
        usuario.setEmail("klisman@email.com");
        usuario.setTelefone("71999999999");
        usuario.setRole(Role.USUARIO);

        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(authenticatedUser.getEmail()).thenReturn("admin@email.com");
        when(authenticatedUser.isAdmin()).thenReturn(true);

        UsuarioResponseDTO response = usuarioService.listarPorId(1L);

        assertNotNull(response); assertEquals(1L, response.id());
        assertEquals("Klisman", response.nome());

        verify(repository).findById(1L);
        verify(authenticatedUser).getEmail();
        verify(authenticatedUser).isAdmin();
    }
    @Test void deveLancarExcecaoQuandoUsuarioTentarListarOutroUsuario() {

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Klisman");
        usuario.setEmail("klisman@email.com");

        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(authenticatedUser.getEmail()).thenReturn("outro@email.com");
        when(authenticatedUser.isAdmin()).thenReturn(false);

        BadRequestException exception = assertThrows( BadRequestException.class,
                () -> usuarioService.listarPorId(1L) );

        assertEquals( "Você só pode visualizar seu próprio perfil", exception.getMessage());

        verify(repository).findById(1L); }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontradoPorId() {

        when(repository.findById(99L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> usuarioService.listarPorId(99L)
        );

        assertEquals("Usuario não encontrado com id: 99", exception.getMessage());

        verify(repository).findById(99L);
    }

    @Test void deveAtualizarUsuarioComSucessoQuandoForProprioUsuario() {

        Usuario existente = new Usuario();
        existente.setId(1L);
        existente.setNome("Klisman");
        existente.setEmail("klisman@email.com");
        existente.setTelefone("71999999999");
        existente.setSenha("senha-antiga");
        existente.setRole(Role.USUARIO);
        existente.setDataCadastro(LocalDateTime.now());

        UsuarioRequestDTO dto = new UsuarioRequestDTO( "Klisman", "klisman.novo@email.com",
                "novaSenha", "71988887777" );

        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(authenticatedUser.getEmail()).thenReturn("klisman@email.com");
        when(authenticatedUser.isAdmin()).thenReturn(false);
        when(repository.existsByEmail(dto.email())).thenReturn(false);
        when(passwordEncoder.encode(dto.senha())).thenReturn("senha-nova-criptografada");
        when(repository.save(any(Usuario.class))).thenReturn(existente);

        UsuarioResponseDTO response = usuarioService.atualizar(1L, dto);

        assertNotNull(response);
        assertEquals("Klisman", response.nome());
        assertEquals("klisman.novo@email.com", response.email());
        assertEquals("71988887777", response.telefone());

        verify(repository).findById(1L);
        verify(repository).existsByEmail(dto.email());
        verify(passwordEncoder).encode(dto.senha());
        verify(repository).save(argThat(u -> u.getSenha().equals("senha-nova-criptografada") ));
    }

    @Test void deveAtualizarUsuarioComSucessoQuandoForAdmin() {

        Usuario existente = new Usuario();
        existente.setId(1L);
        existente.setNome("Klisman");
        existente.setEmail("klisman@email.com");
        existente.setTelefone("71999999999");
        existente.setSenha("senha-antiga");
        existente.setRole(Role.USUARIO);

        UsuarioRequestDTO dto = new UsuarioRequestDTO( "Klisman", "klisman.novo@email.com",
                "novaSenha", "71988887777" );

        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(authenticatedUser.getEmail()).thenReturn("admin@email.com");
        when(authenticatedUser.isAdmin()) .thenReturn(true);
        when(repository.existsByEmail(dto.email())).thenReturn(false);
        when(passwordEncoder.encode(dto.senha())).thenReturn("senha-nova-criptografada");
        when(repository.save(any(Usuario.class))).thenReturn(existente);

        UsuarioResponseDTO response = usuarioService.atualizar(1L, dto);
        assertNotNull(response); verify(repository).save(any(Usuario.class));
    }
    @Test void deveLancarExcecaoQuandoUsuarioTentarAtualizarOutroUsuario() {

        Usuario existente = new Usuario();
        existente.setId(1L);
        existente.setNome("Klisman");
        existente.setEmail("klisman@email.com");
        UsuarioRequestDTO dto = new UsuarioRequestDTO( "Klisman", "novo@email.com",
                "123456", "71999999999" );

        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(authenticatedUser.getEmail()).thenReturn("outro@email.com");
        when(authenticatedUser.isAdmin()).thenReturn(false);

        BadRequestException exception = assertThrows(
                BadRequestException.class, () -> usuarioService.atualizar(1L, dto) );

        assertEquals( "Você só pode atualizar seu proprio perfil", exception.getMessage());

        verify(repository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void deveAtualizarUsuarioSemTrocarEmailSemChecarDuplicidade() {

        Usuario existente = new Usuario();
        existente.setId(1L);
        existente.setNome("Klisman");
        existente.setEmail("klisman@email.com");
        existente.setTelefone("71999999999");
        existente.setSenha("senha-antiga");

        UsuarioRequestDTO dto = new UsuarioRequestDTO("Klisman", "klisman@email.com", "", "71988887777");

        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(authenticatedUser.getEmail()).thenReturn("klisman@email.com");
        when(authenticatedUser.isAdmin()).thenReturn(false);
        when(repository.save(any(Usuario.class))).thenReturn(existente);

        usuarioService.atualizar(1L, dto);

        verify(repository, never()).existsByEmail(any());
    }
    @Test
    void deveAtualizarUsuarioSemAlterarSenhaQuandoSenhaVazia() {

        Usuario existente = new Usuario();
        existente.setId(1L);
        existente.setNome("Klisman");
        existente.setEmail("klisman@email.com");
        existente.setTelefone("71999999999");
        existente.setSenha("senha-antiga");

        UsuarioRequestDTO dto = new UsuarioRequestDTO("Klisman", "klisman@email.com", "", "71988887777");

        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(authenticatedUser.getEmail()).thenReturn("klisman@email.com");
        when(authenticatedUser.isAdmin()).thenReturn(false);
        when(repository.save(any(Usuario.class))).thenReturn(existente);

        usuarioService.atualizar(1L, dto);

        assertEquals("senha-antiga", existente.getSenha());
        verify(passwordEncoder, never()).encode(any());
    }
    @Test
    void deveLancarExcecaoQuandoEmailJaExisteAoAtualizarParaOutroEmail() {

        Usuario existente = new Usuario();
        existente.setId(1L);
        existente.setEmail("klisman@email.com");

        UsuarioRequestDTO dto = new UsuarioRequestDTO("Klisman", "outro@email.com", "123456", "71999999999");

        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(authenticatedUser.getEmail()).thenReturn("klisman@email.com");
        when(authenticatedUser.isAdmin()).thenReturn(false);
        when(repository.existsByEmail(dto.email())).thenReturn(true);

        BadRequestException exception = assertThrows(
                BadRequestException.class, () -> usuarioService.atualizar(1L, dto));

        assertEquals("Email já cadastrado", exception.getMessage());

        verify(repository, never()).save(any());
    }
    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontradoAoAtualizar() {

        UsuarioRequestDTO dto = new UsuarioRequestDTO("Klisman", "klisman@email.com", "123456", "71999999999");

        when(repository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> usuarioService.atualizar(99L, dto));

        verify(repository, never()).save(any());
    }

    @Test void deveDeletarUsuarioComSucessoQuandoForProprioUsuario() {

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("klisman@email.com");

        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(authenticatedUser.getEmail()).thenReturn("klisman@email.com");
        when(authenticatedUser.isAdmin()).thenReturn(false);

        usuarioService.deletar(1L);

        verify(repository).findById(1L);
        verify(repository).delete(usuario);
    }
    @Test void deveDeletarUsuarioComSucessoQuandoForAdmin() {

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("klisman@email.com");

        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(authenticatedUser.getEmail()).thenReturn("admin@email.com");
        when(authenticatedUser.isAdmin()).thenReturn(true);

        usuarioService.deletar(1L);
        verify(repository).delete(usuario);
    }
    @Test void deveLancarExcecaoQuandoUsuarioTentarDeletarOutroUsuario() {

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("klisman@email.com");

        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(authenticatedUser.getEmail()).thenReturn("outro@email.com");
        when(authenticatedUser.isAdmin()).thenReturn(false);

        BadRequestException exception = assertThrows(
                BadRequestException.class, () -> usuarioService.deletar(1L) );

        assertEquals( "Você só pode excluir seu proprio perfil", exception.getMessage());

        verify(repository, never()).delete(any());
    }
    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontradoAoDeletar() {

        when(repository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> usuarioService.deletar(99L));

        verify(repository, never()).delete(any());
    }
}

