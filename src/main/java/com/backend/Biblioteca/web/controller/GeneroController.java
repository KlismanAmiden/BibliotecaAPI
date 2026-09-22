package com.backend.Biblioteca.web.controller;

import com.backend.Biblioteca.application.dto.request.GeneroRequestDTO;
import com.backend.Biblioteca.application.dto.response.GeneroResponseDTO;
import com.backend.Biblioteca.application.service.GeneroService;
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
@RequestMapping("/api/generos")
@RequiredArgsConstructor
@Tag(name = "Gêneros", description = "Cadastro e consulta de gêneros literários")
public class GeneroController {

    private final GeneroService service;

    @GetMapping
    @Operation(summary = "Lista todos os gêneros")
    public List<GeneroResponseDTO> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um gênero pelo id")
    public GeneroResponseDTO listarPorId(@PathVariable long id) {
        return service.listarPorId(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','BIBLIOTECARIO')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Cadastra um gênero", description = "Requer ADMIN ou BIBLIOTECARIO.")
    @ResponseStatus(HttpStatus.CREATED)
    public GeneroResponseDTO criar(@Valid @RequestBody GeneroRequestDTO dto){
        return service.criar(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','BIBLIOTECARIO')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Atualiza um gênero", description = "Requer ADMIN ou BIBLIOTECARIO.")
    public GeneroResponseDTO atualizar (@PathVariable long id,@Valid @RequestBody GeneroRequestDTO dto){
        return service.atualizar(id,dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Remove um gênero", description = "Requer ADMIN.")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable long id){
        service.deletar(id);
    }
}
