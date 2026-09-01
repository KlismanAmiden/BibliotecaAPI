package com.backend.Biblioteca.web.controller;

import com.backend.Biblioteca.application.dto.request.usuarioRequestDTO;
import com.backend.Biblioteca.application.dto.response.usuarioResponseDTO;
import com.backend.Biblioteca.application.service.usuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class usuarioController {

    private final usuarioService service;

    @GetMapping
    public List<usuarioResponseDTO> listar() {
        return service.Listar();
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public usuarioResponseDTO criar (@Valid @RequestBody usuarioRequestDTO dto) {
        return service.criar(dto);
    }
}
