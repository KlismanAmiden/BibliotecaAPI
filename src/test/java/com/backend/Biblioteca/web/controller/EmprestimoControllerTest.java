package com.backend.Biblioteca.web.controller;

import com.backend.Biblioteca.application.dto.request.EmprestimoRequestDTO;
import com.backend.Biblioteca.application.dto.response.EmprestimoResponseDTO;
import com.backend.Biblioteca.application.service.EmprestimoService;
import com.backend.Biblioteca.domain.enums.StatusEmprestimo;
import com.backend.Biblioteca.infrastructure.config.SecurityConfig;
import com.backend.Biblioteca.infrastructure.security.JwtUtil;
import com.backend.Biblioteca.web.exception.BadRequestException;
import com.backend.Biblioteca.web.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AutorController.class)
@Import(SecurityConfig.class)
public class EmprestimoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EmprestimoService service;

    @MockitoBean
    private JwtUtil jwtUtil;

    private EmprestimoResponseDTO emprestimoResponse() {
        return new EmprestimoResponseDTO(
                1L, 1L, Set.of(1L),
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(7),
                null,
                StatusEmprestimo.ATIVO,
                BigDecimal.ZERO
        );
    }
    private EmprestimoRequestDTO emprestimoRequestValido() {
        return new EmprestimoRequestDTO(1L, Set.of(1L), LocalDateTime.now().plusDays(7));
    }

    @Test
    void listarTodosDeveRetornar401QuandoNaoAutenticado() throws Exception {

        mockMvc.perform(get("/api/emprestimos"))
                .andExpect(status().isUnauthorized());
    }
    @Test
    @WithMockUser(roles = "USUARIO")
    void listarTodosDeveRetornar403ParaUsuarioComum() throws Exception {

        mockMvc.perform(get("/api/emprestimos"))
                .andExpect(status().isForbidden());
    }
    @Test
    @WithMockUser(roles = "BIBLIOTECARIO")
    void listarTodosDeveRetornar200ParaBibliotecario() throws Exception {

        when(service.listarTodos()).thenReturn(List.of(emprestimoResponse()));

        mockMvc.perform(get("/api/emprestimos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void buscarPorIdDeveRetornar200ParaAdmin() throws Exception {

        when(service.buscarPorId(1L)).thenReturn(emprestimoResponse());

        mockMvc.perform(get("/api/emprestimos/1"))
                .andExpect(status().isOk());
    }
    @Test
    @WithMockUser(roles = "USUARIO")
    void buscarPorIdDeveRetornar403ParaUsuarioComum() throws Exception {

        mockMvc.perform(get("/api/emprestimos/1"))
                .andExpect(status().isForbidden());

        verify(service, never()).buscarPorId(any());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void buscarPorIdDeveRetornar404QuandoNaoEncontrado() throws Exception {

        when(service.buscarPorId(99L))
                .thenThrow(new ResourceNotFoundException("Empréstimo não encontrado com id: 99"));

        mockMvc.perform(get("/api/emprestimos/99"))
                .andExpect(status().isNotFound());
    }
    @Test
    void listarPorUsuarioDeveRetornar401QuandoNaoAutenticado() throws Exception {

        mockMvc.perform(get("/api/emprestimos/usuario/1"))
                .andExpect(status().isUnauthorized());
    }
    @Test
    @WithMockUser(roles = "USUARIO")
    void listarPorUsuarioDeveRetornar200ParaQualquerAutenticado() throws Exception {

        when(service.listarPorUsuario(1L)).thenReturn(List.of(emprestimoResponse()));

        mockMvc.perform(get("/api/emprestimos/usuario/1"))
                .andExpect(status().isOk());
    }
    @Test
    @WithMockUser(roles = "USUARIO")
    void listarPorUsuarioDeveRetornar400QuandoRegraDeNegocioBarrar() throws Exception {

        when(service.listarPorUsuario(2L))
                .thenThrow(new BadRequestException("Você só pode consultar seus próprios empréstimos."));

        mockMvc.perform(get("/api/emprestimos/usuario/2"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Você só pode consultar seus próprios empréstimos."));
    }
    @Test
    void criarDeveRetornar401QuandoNaoAutenticado() throws Exception {

        mockMvc.perform(post("/api/emprestimos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emprestimoRequestValido())))
                .andExpect(status().isUnauthorized());

        verify(service, never()).criar(any());
    }

}
