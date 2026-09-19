package com.backend.Biblioteca.web.controller;

import com.backend.Biblioteca.application.dto.request.GeneroRequestDTO;
import com.backend.Biblioteca.application.dto.response.GeneroResponseDTO;
import com.backend.Biblioteca.application.service.GeneroService;
import com.backend.Biblioteca.infrastructure.config.SecurityConfig;
import com.backend.Biblioteca.infrastructure.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(GeneroController.class)
@Import(SecurityConfig.class)
public class GeneroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GeneroService service;

    @MockitoBean
    private JwtUtil jwtUtil;

    private GeneroResponseDTO generoResponse() {
        return new GeneroResponseDTO(1L, "Romance", "Ficção narrativa centrada em relações e personagens");
    }

    private GeneroRequestDTO generoRequestValido() {
        return new GeneroRequestDTO("Romance", "Ficção narrativa centrada em relações e personagens");
    }

}
