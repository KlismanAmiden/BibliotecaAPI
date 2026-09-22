package com.backend.Biblioteca.web.controller;

import com.backend.Biblioteca.application.dto.request.ExemplarRequestDTO;
import com.backend.Biblioteca.application.dto.response.ExemplarResponseDTO;
import com.backend.Biblioteca.application.service.ExemplarService;
import com.backend.Biblioteca.domain.enums.StatusExemplar;
import com.backend.Biblioteca.web.exception.BadRequestException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Exemplares", description = "Cadastro e controle de status dos exemplares físicos")
public class ExemplarController {

    private final ExemplarService service;

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Lista todos os exemplares", description = "Requer usuário autenticado.")
    public List<ExemplarResponseDTO> listarTodos(){
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Busca um exemplar pelo id", description = "Requer usuário autenticado.")
    public ExemplarResponseDTO buscarPorId(@PathVariable Long id){
        return service.listarPorId(id);
    }

    @GetMapping("/livro/{livroId}")
    @Operation(summary = "Lista exemplares de um livro", description = "Endpoint público.")
    public  List<ExemplarResponseDTO> buscarPorLivro(@PathVariable Long livroId){
        return service.listarPorLivro(livroId);
    }
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','BIBLIOTECARIO')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Cadastra um exemplar", description = "Requer ADMIN ou BIBLIOTECARIO.")
    @ResponseStatus(HttpStatus.CREATED)
    public ExemplarResponseDTO criar(@Valid @RequestBody ExemplarRequestDTO dto) {
        return service.criar(dto);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','BIBLIOTECARIO')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Atualiza o status de um exemplar", description = "Requer ADMIN ou BIBLIOTECARIO. Valores aceitos: DISPONIVEL, EMPRESTADO, INDISPONIVEL.")
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
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Remove um exemplar", description = "Requer ADMIN.")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}
