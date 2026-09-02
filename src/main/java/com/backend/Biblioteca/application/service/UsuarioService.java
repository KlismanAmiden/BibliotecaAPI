package com.backend.Biblioteca.application.service;
import com.backend.Biblioteca.application.dto.request.UsuarioRequestDTO;
import com.backend.Biblioteca.application.dto.response.UsuarioResponseDTO;
import com.backend.Biblioteca.domain.model.Usuario;
import com.backend.Biblioteca.web.exception.BadRequestException;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.backend.Biblioteca.infrastructure.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder){
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }
    public List<UsuarioResponseDTO> Listar(){
        return repository.findAll().stream().map(this::toDTO).toList();
    }
    public UsuarioResponseDTO criar(UsuarioRequestDTO dto){
        if(repository.existsByEmail(dto.email())){
            throw new BadRequestException("Email já cadastrado");
        }
        Usuario usuario = new Usuario();
        usuario.setEmail(dto.email());
        usuario.setNome(dto.nome());
        usuario.setTelefone(dto.telefone());
        usuario.setDataCadastro(LocalDateTime.now());
        usuario.setSenha(passwordEncoder.encode(dto.senha()));
        Usuario salvo = repository.save(usuario);
        return toDTO(salvo);
    }
    private UsuarioResponseDTO toDTO(Usuario u){
        return new UsuarioResponseDTO(
                u.getId(),
                u.getNome(),
                u.getEmail(),
                u.getTelefone(),
                u.getDataCadastro(),
                u.isAtivo()
        );
    }


}
