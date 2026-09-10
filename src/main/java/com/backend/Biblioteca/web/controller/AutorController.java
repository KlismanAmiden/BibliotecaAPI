package com.backend.Biblioteca.web.controller;

import com.backend.Biblioteca.application.dto.request.AutorRequestDTO;
import com.backend.Biblioteca.application.dto.response.AutorResponseDTO;
import com.backend.Biblioteca.application.service.AutorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/Autores")
@RequiredArgsConstructor
public class AutorController {

    private final AutorService service;

    @GetMapping
    public List<AutorResponseDTO> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public AutorResponseDTO listarPorId(@PathVariable long id) {
        return service.listarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AutorResponseDTO criar(@RequestBody AutorRequestDTO dto){
        return service.criar(dto);
    }

    @PutMapping("/{id}")
    public AutorResponseDTO atualizar (@PathVariable long id,@RequestBody AutorRequestDTO dto){
        return service.atualizar(id,dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable long id){}

}
