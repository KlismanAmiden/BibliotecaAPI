package com.backend.Biblioteca.web.controller;

import com.backend.Biblioteca.application.dto.request.ExemplarRequestDTO;
import com.backend.Biblioteca.application.dto.response.ExemplarResponseDTO;
import com.backend.Biblioteca.application.service.ExemplarService;
import com.backend.Biblioteca.domain.enums.StatusExemplar;
import com.backend.Biblioteca.infrastructure.config.SecurityConfig;
import com.backend.Biblioteca.infrastructure.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExemplarControllerTest.class)
@Import(SecurityConfig.class)
public class ExemplarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ExemplarService service;

    @MockitoBean
    private JwtUtil jwtUtil;

    private ExemplarResponseDTO exemplarResponse() {
        return new ExemplarResponseDTO(1L, 1L, StatusExemplar.DISPONIVEL);
    }

    private ExemplarRequestDTO exemplarRequestValido() {
        return new ExemplarRequestDTO(1L);
    }
    @Test
    void listarTodosDeveRetornar401QuandoNaoAutenticado() throws Exception {

        mockMvc.perform(get("/api/exemplares"))
                .andExpect(status().isUnauthorized());
    }
}
