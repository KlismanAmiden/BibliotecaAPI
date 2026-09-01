package com.backend.Biblioteca.application.service;
import com.backend.Biblioteca.application.dto.request.usuarioRequestDTO;
import com.backend.Biblioteca.application.dto.response.usuarioResponseDTO;
import com.backend.Biblioteca.domain.model.usuario;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.backend.Biblioteca.infrastructure.repository.usuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class usuarioService {

    private final usuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public usuarioService(usuarioRepository repository, PasswordEncoder passwordEncoder){
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }
    public List<usuarioResponseDTO> Listar(){
        return repository.findAll().stream().map(this::toDTO).toList();
    }
    public usuarioResponseDTO criar(usuarioRequestDTO dto){
        if(repository.existsByEmail(dto.email())){
            throw new RuntimeException("Email já cadastrado");
        }
        usuario usuario = new usuario();
        usuario.setEmail(dto.email());
        usuario.setNome(dto.nome());
        usuario.setSenha(passwordEncoder.encode(dto.senha()));
        usuario salvo = repository.save(usuario);
        return toDTO(salvo);
    }
    private usuarioResponseDTO toDTO(usuario u){
        return new usuarioResponseDTO(
                u.getId(),
                u.getNome(),
                u.getEmail(),
                u.getTelefone(),
                u.getDataCadastro(),
                u.isAtivo()
        );
    }


}
