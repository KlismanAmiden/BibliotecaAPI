package com.backend.Biblioteca.web.controller;

import com.backend.Biblioteca.application.dto.request.AutorRequestDTO;
import com.backend.Biblioteca.application.dto.response.AutorResponseDTO;
import com.backend.Biblioteca.application.service.AutorService;
import com.backend.Biblioteca.infrastructure.config.SecurityConfig;
import com.backend.Biblioteca.infrastructure.security.JwtUtil;
import com.backend.Biblioteca.web.exception.ResourceNotFoundException;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AutorController.class)
@Import(SecurityConfig.class)
public class AutorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AutorService service;

    @MockitoBean
    private JwtUtil jwtUtil;

    private AutorResponseDTO autorResponse() {
        return new AutorResponseDTO(1L, "Machado de Assis", "Escritor brasileiro", 1839, "Brasileira");
    }

    private AutorRequestDTO autorRequestValido() {
        return new AutorRequestDTO("Machado de Assis", "Escritor brasileiro", 1839, "Brasileira");
    }
    @Test
    void listarTodosDeveSerPublicoERetornar200() throws Exception {

        when(service.listarTodos()).thenReturn(List.of(autorResponse()));

        mockMvc.perform(get("/api/autores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Machado de Assis"));
    }
    @Test
    void listarPorIdDeveSerPublicoERetornar200() throws Exception {

        when(service.listarPorId(1L)).thenReturn(autorResponse());

        mockMvc.perform(get("/api/autores/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }
    @Test
    void listarPorIdDeveRetornar404QuandoNaoEncontrado() throws Exception {

        when(service.listarPorId(99L))
                .thenThrow(new ResourceNotFoundException("Autor não encontrado com id: 99"));

        mockMvc.perform(get("/api/autores/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Autor não encontrado com id: 99"));
    }
    @Test
    void criarDeveRetornar401QuandoNaoAutenticado() throws Exception {

        mockMvc.perform(post("/api/autores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(autorRequestValido())))
                .andExpect(status().isUnauthorized());

        verify(service, never()).criar(any());
    }
    @Test
    @WithMockUser(roles = "USUARIO")
    void criarDeveRetornar403QuandoRoleNaoAutorizada() throws Exception {

        mockMvc.perform(post("/api/autores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(autorRequestValido())))
                .andExpect(status().isForbidden());

        verify(service, never()).criar(any());
    }
    @Test
    @WithMockUser(roles = "BIBLIOTECARIO")
    void criarDeveRetornar201QuandoRoleAutorizada() throws Exception {

        when(service.criar(any(AutorRequestDTO.class))).thenReturn(autorResponse());

        mockMvc.perform(post("/api/autores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(autorRequestValido())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Machado de Assis"));
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void criarDeveRetornar400QuandoDtoInvalido() throws Exception {

        AutorRequestDTO dtoInvalido = new AutorRequestDTO("", "Bio", 900, "");

        mockMvc.perform(post("/api/autores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest());

        verify(service, never()).criar(any());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void atualizarDeveRetornar200QuandoAutorizado() throws Exception {

        when(service.atualizar(eq(1L), any(AutorRequestDTO.class))).thenReturn(autorResponse());

        mockMvc.perform(put("/api/autores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(autorRequestValido())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }
    @Test
    @WithMockUser(roles = "USUARIO")
    void atualizarDeveRetornar403QuandoRoleNaoAutorizada() throws Exception {

        mockMvc.perform(put("/api/autores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(autorRequestValido())))
                .andExpect(status().isForbidden());

        verify(service, never()).atualizar(any(), any());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void deletarDeveRetornar204QuandoAutorizado() throws Exception {

        mockMvc.perform(delete("/api/autores/1"))
                .andExpect(status().isNoContent());

        verify(service).deletar(1L);
    }
    @Test
    @WithMockUser(roles = "BIBLIOTECARIO")
    void deletarDeveRetornar403QuandoRoleNaoAutorizada() throws Exception {

        mockMvc.perform(delete("/api/autores/1"))
                .andExpect(status().isForbidden());

        verify(service, never()).deletar(any());
    }
}
