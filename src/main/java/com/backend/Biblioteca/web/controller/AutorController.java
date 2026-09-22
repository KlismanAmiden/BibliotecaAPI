package com.backend.Biblioteca.web.controller;

import com.backend.Biblioteca.application.dto.request.AutorRequestDTO;
import com.backend.Biblioteca.application.dto.response.AutorResponseDTO;
import com.backend.Biblioteca.application.service.AutorService;
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
@RequestMapping("/api/autores")
@RequiredArgsConstructor
@Tag(name = "Autores", description = "Cadastro e consulta de autores")
public class AutorController {

    private final AutorService service;

    @GetMapping
    @Operation(summary = "Lista todos os autores", description = "Endpoint público, não exige autenticação.")
    public List<AutorResponseDTO> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um autor pelo id", description = "Endpoint público, não exige autenticação.")
    public AutorResponseDTO listarPorId(@PathVariable long id) {
        return service.listarPorId(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','BIBLIOTECARIO')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Cadastra um novo autor", description = "Requer role ADMIN ou BIBLIOTECARIO.")
    @ResponseStatus(HttpStatus.CREATED)
    public AutorResponseDTO criar(@Valid @RequestBody AutorRequestDTO dto){
        return service.criar(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','BIBLIOTECARIO')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Atualiza um autor existente", description = "Requer role ADMIN ou BIBLIOTECARIO.")
    public AutorResponseDTO atualizar (@PathVariable long id,@RequestBody AutorRequestDTO dto){
        return service.atualizar(id,dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Remove um autor", description = "Requer role ADMIN.")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable long id){
        service.deletar(id);
    }

}
