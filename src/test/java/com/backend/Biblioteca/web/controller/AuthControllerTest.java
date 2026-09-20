package com.backend.Biblioteca.web.controller;

import com.backend.Biblioteca.application.dto.request.LoginRequestDTO;
import com.backend.Biblioteca.application.dto.response.LoginResponseDTO;
import com.backend.Biblioteca.application.service.AuthService;
import com.backend.Biblioteca.infrastructure.config.SecurityConfig;
import com.backend.Biblioteca.infrastructure.security.JwtUtil;
import com.backend.Biblioteca.web.exception.BadRequestException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService service;

    @MockitoBean
    private JwtUtil jwtUtil;

    private LoginRequestDTO loginRequestValido(){
        return new LoginRequestDTO("klisman@email.com","123456");
    }
    @Test
    void loginDeveSerPublico() throws Exception {

        when(service.login(any(LoginRequestDTO.class)))
                .thenReturn(new LoginResponseDTO("token-fake", 1L, "Klisman", "klisman@email.com"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestValido())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-fake"))
                .andExpect(jsonPath("$.email").value("klisman@email.com"));
    }
    @Test
    void loginDeveRetornar400QuandoCredenciaisInvalidas() throws Exception {

        when(service.login(any(LoginRequestDTO.class)))
                .thenThrow(new BadRequestException("Email ou Senha inválidos"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestValido())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email ou Senha inválidos"));
    }

}
