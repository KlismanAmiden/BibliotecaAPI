package com.backend.Biblioteca.web.controller;

import com.backend.Biblioteca.application.dto.request.UsuarioRequestDTO;
import com.backend.Biblioteca.application.dto.response.UsuarioResponseDTO;
import com.backend.Biblioteca.application.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Cadastro e gestão de usuários")
public class UsuarioController {

    private final UsuarioService service;

    @GetMapping
    @PreAuthorize(("hasAnyRole('ADMIN','BIBLIOTECARIO')"))
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Lista todos os usuários", description = "Requer ADMIN ou BIBLIOTECARIO.")
    public List<UsuarioResponseDTO> listarTodos() {
        return service.ListarTodos();
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Busca um usuário pelo id", description = "O próprio usuário ou ADMIN podem consultar.")
    public UsuarioResponseDTO listarPorId(@PathVariable long id) {
        return service.listarPorId(id);
    }

    @PostMapping
    @Operation(summary = "Cadastra um novo usuário", description = "Endpoint público — é o registro/signup.")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponseDTO criar (@Valid @RequestBody UsuarioRequestDTO dto) {
        return service.criar(dto);
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Atualiza um usuário", description = "O próprio usuário ou ADMIN podem atualizar.")
    public UsuarioResponseDTO atualizar (@PathVariable long id, @Valid @RequestBody UsuarioRequestDTO dto){
        return service.atualizar(id,dto);
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Remove um usuário", description = "O próprio usuário ou ADMIN podem excluir.")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable long id){
        service.deletar(id);
    }
}
