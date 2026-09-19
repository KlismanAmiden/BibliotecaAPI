package com.backend.Biblioteca.web.controller;

import com.backend.Biblioteca.application.dto.request.EmprestimoRequestDTO;
import com.backend.Biblioteca.application.dto.response.EmprestimoResponseDTO;
import com.backend.Biblioteca.application.service.EmprestimoService;
import com.backend.Biblioteca.domain.enums.StatusEmprestimo;
import com.backend.Biblioteca.infrastructure.config.SecurityConfig;
import com.backend.Biblioteca.infrastructure.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
}
