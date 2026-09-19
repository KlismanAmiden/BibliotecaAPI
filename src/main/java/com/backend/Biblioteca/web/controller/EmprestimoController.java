package com.backend.Biblioteca.web.controller;

import com.backend.Biblioteca.application.dto.request.EmprestimoRequestDTO;
import com.backend.Biblioteca.application.dto.response.EmprestimoResponseDTO;
import com.backend.Biblioteca.application.service.EmprestimoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emprestimos")
@RequiredArgsConstructor
public class EmprestimoController {

    private final EmprestimoService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','BIBLIOTECARIO')")
    public List<EmprestimoResponseDTO> listarTodos(){
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','BIBLIOTECARIO')")
    public EmprestimoResponseDTO buscarPorId(@PathVariable Long id){
        return service.buscarPorId(id);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<EmprestimoResponseDTO> listarPorUsuario(@PathVariable Long usuarioId){
        return service.listarPorUsuario(usuarioId);
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmprestimoResponseDTO criar(@Valid @RequestBody EmprestimoRequestDTO dto) {
        return service.criar(dto);
    }

    @PatchMapping("/{id}/devolver")
    @PreAuthorize("hasAnyRole('ADMIN','BIBLIOTECARIO')")
    public EmprestimoResponseDTO devolver(@PathVariable Long id){
        return service.devolver(id);
    }
}
