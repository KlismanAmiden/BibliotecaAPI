package com.backend.Biblioteca.web.controller;

import com.backend.Biblioteca.application.dto.request.EmprestimoRequestDTO;
import com.backend.Biblioteca.application.dto.response.EmprestimoResponseDTO;
import com.backend.Biblioteca.application.service.EmprestimoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/Emprestimos")
@RequiredArgsConstructor
public class EmprestimoController {

    private final EmprestimoService service;

    @GetMapping
    public List<EmprestimoResponseDTO> listarTodos(){
        return service.listarTodos();
    }
    @GetMapping("/{Id}")
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
    @PatchMapping("/{Id}/devolver")
    public EmprestimoResponseDTO devolver(@PathVariable Long id){
        return service.devolver(id);
    }


}
