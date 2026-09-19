package com.backend.Biblioteca.web.controller;

import com.backend.Biblioteca.application.dto.request.GeneroRequestDTO;
import com.backend.Biblioteca.application.dto.response.GeneroResponseDTO;
import com.backend.Biblioteca.application.service.GeneroService;
import com.backend.Biblioteca.infrastructure.config.SecurityConfig;
import com.backend.Biblioteca.infrastructure.security.JwtUtil;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


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
    @Test
    void listarTodosDeveSerPublico() throws Exception {

        when(service.listarTodos()).thenReturn(List.of(generoResponse()));

        mockMvc.perform(get("/api/generos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Romance"));
    }

    @Test
    void listarPorIdDeveSerPublico() throws Exception {

        when(service.listarPorId(1L)).thenReturn(generoResponse());

        mockMvc.perform(get("/api/generos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void listarPorIdDeveRetornar404QuandoNaoEncontrado() throws Exception {

        when(service.listarPorId(99L))
                .thenThrow(new ResourceNotFoundException("Genero não encontrado com id: 99"));

        mockMvc.perform(get("/api/generos/99"))
                .andExpect(status().isNotFound());
    }
    @Test
    void criarDeveRetornar401QuandoNaoAutenticado() throws Exception {

        mockMvc.perform(post("/api/generos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(generoRequestValido())))
                .andExpect(status().isUnauthorized());

        verify(service, never()).criar(any());
    }
    @Test
    @WithMockUser(roles = "USUARIO")
    void criarDeveRetornar403QuandoRoleNaoAutorizada() throws Exception {

        mockMvc.perform(post("/api/generos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(generoRequestValido())))
                .andExpect(status().isForbidden());

        verify(service, never()).criar(any());
    }
    @Test
    @WithMockUser(roles = "BIBLIOTECARIO")
    void criarDeveRetornar201QuandoAutorizado() throws Exception {

        when(service.criar(any(GeneroRequestDTO.class))).thenReturn(generoResponse());

        mockMvc.perform(post("/api/generos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(generoRequestValido())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Romance"));
    }
    @Test
    @WithMockUser(roles = "BIBLIOTECARIO")
    void atualizarDeveRetornar200QuandoAutorizado() throws Exception {

        when(service.atualizar(eq(1L), any(GeneroRequestDTO.class))).thenReturn(generoResponse());

        mockMvc.perform(put("/api/generos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(generoRequestValido())))
                .andExpect(status().isOk());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void atualizarDeveRetornar400QuandoDtoInvalido() throws Exception {

        GeneroRequestDTO dtoInvalido = new GeneroRequestDTO("", "");

        mockMvc.perform(put("/api/generos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest());

        verify(service, never()).atualizar(any(), any());
    }
}
