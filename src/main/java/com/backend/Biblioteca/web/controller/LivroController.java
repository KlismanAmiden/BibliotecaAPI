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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LivroResponseDTO criar(@Valid @RequestBody LivroRequestDTO dto) {
        return service.criar(dto);
    }

}
