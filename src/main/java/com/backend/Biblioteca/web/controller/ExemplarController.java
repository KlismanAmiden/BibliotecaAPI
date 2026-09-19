package com.backend.Biblioteca.web.controller;

import com.backend.Biblioteca.application.dto.request.ExemplarRequestDTO;
import com.backend.Biblioteca.application.dto.response.ExemplarResponseDTO;
import com.backend.Biblioteca.application.service.ExemplarService;
import com.backend.Biblioteca.domain.enums.StatusExemplar;
import com.backend.Biblioteca.web.exception.BadRequestException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/exemplares")
@RequiredArgsConstructor
public class ExemplarController {

    private final ExemplarService service;

    @GetMapping
    public List<ExemplarResponseDTO> listarTodos(){
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public ExemplarResponseDTO buscarPorId(@PathVariable Long id){
        return service.listarPorId(id);
    }

    @GetMapping("/livro/{livroId}")
    public  List<ExemplarResponseDTO> buscarPorLivro(@PathVariable Long livroId){
        return service.listarPorLivro(livroId);
    }
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','BIBLIOTECARIO')")
    @ResponseStatus(HttpStatus.CREATED)
    public ExemplarResponseDTO criar(@Valid @RequestBody ExemplarRequestDTO dto) {
        return service.criar(dto);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','BIBLIOTECARIO')")
    public ExemplarResponseDTO atualizarStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String statusStr = body.get("status");
        if (statusStr == null || statusStr.isBlank()) {
            throw new BadRequestException("O campo 'status' é obrigatório.");
        }
        StatusExemplar status;
        try {
            status = StatusExemplar.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Status inválido: " + statusStr);
        }
        return service.atualizarStatus(id, status);
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}
