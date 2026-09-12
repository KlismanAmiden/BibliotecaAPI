package com.backend.Biblioteca.web.controller;

import com.backend.Biblioteca.application.dto.request.ExemplarRequestDTO;
import com.backend.Biblioteca.application.dto.response.ExemplarResponseDTO;
import com.backend.Biblioteca.application.service.ExemplarService;
import com.backend.Biblioteca.domain.enums.StatusExemplar;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/Exemplares")
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
    @ResponseStatus(HttpStatus.CREATED)
    public ExemplarResponseDTO criar(@Valid @RequestBody ExemplarRequestDTO dto) {
        return service.criar(dto);
    }
    @PatchMapping("/{id}/status")
    public ExemplarResponseDTO atualizarStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        StatusExemplar status = StatusExemplar.valueOf(body.get("status").toUpperCase());
        return service.atualizarStatus(id, status);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}
