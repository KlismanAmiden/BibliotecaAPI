package com.backend.Biblioteca.web.controller;

import com.backend.Biblioteca.application.dto.request.UsuarioRequestDTO;
import com.backend.Biblioteca.application.dto.response.UsuarioResponseDTO;
import com.backend.Biblioteca.application.service.UsuarioService;
import com.backend.Biblioteca.domain.enums.Role;
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

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsuarioController.class)
@Import(SecurityConfig.class)
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UsuarioService service;

    @MockitoBean
    private JwtUtil jwtUtil;

    private UsuarioResponseDTO usuarioResponse() {
        return new UsuarioResponseDTO(1L, "Klisman", "klisman@email.com", "71999999999",
                LocalDateTime.now(), true, Role.USUARIO);
    }

    private UsuarioRequestDTO usuarioRequestValido() {
        return new UsuarioRequestDTO("Klisman", "klisman@email.com", "123456", "71999999999");
    }
    @Test
    void listarTodosDeveRetornar401QuandoNaoAutenticado() throws Exception {

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isUnauthorized());
    }
    @Test
    @WithMockUser(roles = "USUARIO")
    void listarTodosDeveRetornar403ParaUsuarioComum() throws Exception {

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isForbidden());
    }
}
