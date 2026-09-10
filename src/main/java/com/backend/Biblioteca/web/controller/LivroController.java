package com.backend.Biblioteca.web.controller;

import com.backend.Biblioteca.application.dto.request.LivroRequestDTO;
import com.backend.Biblioteca.application.dto.response.LivroResponseDTO;
import com.backend.Biblioteca.application.service.LivroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/Livros")
@RequiredArgsConstructor
public class LivroController {

    private final LivroService service;

    @GetMapping
    public List<LivroResponseDTO> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/genero/{generoId}")
    public List<LivroResponseDTO> listarPorGenero(@PathVariable Long generoId) {
        return service.listarPorGenero(generoId);
    }

    @GetMapping("/autor/{autorId}")
    public List<LivroResponseDTO> listarPorAutor(@PathVariable Long autorId) {
        return service.listarPorAutor(autorId);
    }

    @GetMapping("/{id}")
    public LivroResponseDTO buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LivroResponseDTO criar(@Valid @RequestBody LivroRequestDTO dto) {
        return service.criar(dto);
    }

    @PutMapping("/{id}")
    public LivroResponseDTO atualizar (@PathVariable long id,@Valid @RequestBody LivroRequestDTO dto){
        return service.atualizar(id,dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}
