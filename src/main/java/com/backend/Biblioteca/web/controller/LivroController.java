package com.backend.Biblioteca.web.controller;

import com.backend.Biblioteca.application.dto.request.LivroRequestDTO;
import com.backend.Biblioteca.application.dto.response.LivroResponseDTO;
import com.backend.Biblioteca.application.service.LivroService;
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
@RequestMapping("/api/livros")
@RequiredArgsConstructor
@Tag(name = "Livros", description = "Cadastro e consulta de livros")
public class LivroController {

    private final LivroService service;

    @GetMapping
    @Operation(summary = "Lista todos os livros", description = "Endpoint público, não exige autenticação.")
    public List<LivroResponseDTO> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/genero/{generoId}")
    @Operation(summary = "Lista livros de um gênero", description = "Endpoint público, não exige autenticação.")
    public List<LivroResponseDTO> listarPorGenero(@PathVariable Long generoId) {
        return service.listarPorGenero(generoId);
    }

    @GetMapping("/autor/{autorId}")
    @Operation(summary = "Lista livros de um autor", description = "Endpoint público, não exige autenticação.")
    public List<LivroResponseDTO> listarPorAutor(@PathVariable Long autorId) {
        return service.listarPorAutor(autorId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um livro pelo id", description = "Endpoint público, não exige autenticação.")
    public LivroResponseDTO buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','BIBLIOTECARIO')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Cadastra um livro", description = "Requer ADMIN ou BIBLIOTECARIO.")
    @ResponseStatus(HttpStatus.CREATED)
    public LivroResponseDTO criar(@Valid @RequestBody LivroRequestDTO dto) {
        return service.criar(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','BIBLIOTECARIO')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Atualiza um livro", description = "Requer ADMIN ou BIBLIOTECARIO.")
    public LivroResponseDTO atualizar (@PathVariable long id,@Valid @RequestBody LivroRequestDTO dto){
        return service.atualizar(id,dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Remove um livro", description = "Requer ADMIN.")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}
