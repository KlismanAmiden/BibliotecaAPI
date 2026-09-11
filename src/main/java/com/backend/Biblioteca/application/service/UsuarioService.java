package com.backend.Biblioteca.application.service;

import com.backend.Biblioteca.application.dto.request.UsuarioRequestDTO;
import com.backend.Biblioteca.application.dto.response.UsuarioResponseDTO;
import com.backend.Biblioteca.domain.model.Usuario;
import com.backend.Biblioteca.infrastructure.repository.UsuarioRepository;
import com.backend.Biblioteca.web.exception.BadRequestException;
import com.backend.Biblioteca.web.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public List<UsuarioResponseDTO> ListarTodos(){
        return repository.findAll().stream().map(this::toDTO).toList();
    }

    public UsuarioResponseDTO listarPorId(Long id){
        Usuario usuario = buscarPorEntidade(id);
        return toDTO(usuario);
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

    public UsuarioResponseDTO atualizar(Long id, UsuarioRequestDTO dto){
        Usuario usuario = buscarPorEntidade(id);
        if(!usuario.getEmail().equals(dto.email()) && repository.existsByEmail(dto.email())){
            throw new BadRequestException("Email já cadastrado");
        }
        usuario.setEmail(dto.email());
        usuario.setNome(dto.nome());
        usuario.setTelefone(dto.telefone());
        if(dto.senha() != null && !dto.senha().isEmpty()){
            usuario.setSenha(passwordEncoder.encode(dto.senha()));
        }
        Usuario salvo = repository.save(usuario);
        return toDTO(salvo);
    }
    public void deletar(Long id){
        Usuario usuario = buscarPorEntidade(id);
        repository.delete(usuario);
    }

    //-------------------------------Métodos auxiliares privados-----------------------------------------

    private Usuario buscarPorEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario não encontrado com id: " + id));
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
