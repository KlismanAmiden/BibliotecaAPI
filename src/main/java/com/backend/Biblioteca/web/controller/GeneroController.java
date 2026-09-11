package com.backend.Biblioteca.web.controller;

import com.backend.Biblioteca.application.dto.request.GeneroRequestDTO;
import com.backend.Biblioteca.application.dto.response.GeneroResponseDTO;
import com.backend.Biblioteca.application.service.GeneroService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/Generos")
@RequiredArgsConstructor
public class GeneroController {

    private final GeneroService service;
    @GetMapping
    public List<GeneroResponseDTO> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public GeneroResponseDTO listarPorId(@PathVariable long id) {
        return service.listarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GeneroResponseDTO criar(@RequestBody GeneroRequestDTO dto){
        return service.criar(dto);
    }

    @PutMapping("/{id}")
    public GeneroResponseDTO atualizar (@PathVariable long id,@RequestBody GeneroRequestDTO dto){
        return service.atualizar(id,dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable long id){}
}
