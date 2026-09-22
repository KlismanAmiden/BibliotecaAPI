package com.backend.Biblioteca.web.controller;

import com.backend.Biblioteca.application.dto.request.EmprestimoRequestDTO;
import com.backend.Biblioteca.application.dto.response.EmprestimoResponseDTO;
import com.backend.Biblioteca.application.service.EmprestimoService;
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
@RequestMapping("/api/emprestimos")
@RequiredArgsConstructor
@Tag(name = "Empréstimos", description = "Criação, consulta e devolução de empréstimos")
public class EmprestimoController {

    private final EmprestimoService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','BIBLIOTECARIO')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Lista todos os empréstimos", description = "Requer ADMIN ou BIBLIOTECARIO.")
    @SecurityRequirement(name = "bearerAuth")
    public List<EmprestimoResponseDTO> listarTodos(){
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','BIBLIOTECARIO')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Busca um empréstimo pelo id", description = "Requer ADMIN ou BIBLIOTECARIO.")
    public EmprestimoResponseDTO buscarPorId(@PathVariable Long id){
        return service.buscarPorId(id);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<EmprestimoResponseDTO> listarPorUsuario(@PathVariable Long usuarioId){
        return service.listarPorUsuario(usuarioId);
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Cria um empréstimo", description = "Qualquer usuário autenticado pode criar empréstimo para si mesmo; ADMIN/BIBLIOTECARIO podem criar para qualquer usuário.")
    public EmprestimoResponseDTO criar(@Valid @RequestBody EmprestimoRequestDTO dto) {
        return service.criar(dto);
    }

    @PatchMapping("/{id}/devolver")
    @PreAuthorize("hasAnyRole('ADMIN','BIBLIOTECARIO')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Registra a devolução de um empréstimo", description = "Requer ADMIN ou BIBLIOTECARIO.")
    public EmprestimoResponseDTO devolver(@PathVariable Long id){
        return service.devolver(id);
    }
}
