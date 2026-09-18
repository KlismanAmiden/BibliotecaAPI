package com.backend.Biblioteca.web.controller;

import com.backend.Biblioteca.application.dto.request.AutorRequestDTO;
import com.backend.Biblioteca.application.dto.response.AutorResponseDTO;
import com.backend.Biblioteca.application.service.AutorService;
import com.backend.Biblioteca.infrastructure.config.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(AutorController.class)
@Import(SecurityConfig.class)
public class AutorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AutorService service;

    private AutorResponseDTO autorResponse() {
        return new AutorResponseDTO(1L, "Machado de Assis", "Escritor brasileiro", 1839, "Brasileira");
    }

    private AutorRequestDTO autorRequestValido() {
        return new AutorRequestDTO("Machado de Assis", "Escritor brasileiro", 1839, "Brasileira");
    }
}
