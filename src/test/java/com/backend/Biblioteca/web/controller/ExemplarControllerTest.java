package com.backend.Biblioteca.web.controller;

import com.backend.Biblioteca.application.dto.request.ExemplarRequestDTO;
import com.backend.Biblioteca.application.dto.response.ExemplarResponseDTO;
import com.backend.Biblioteca.application.service.ExemplarService;
import com.backend.Biblioteca.domain.enums.StatusExemplar;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    @Test
    @WithMockUser(roles = "USUARIO")
    void listarTodosDeveRetornar200QuandoAutenticado() throws Exception {

        when(service.listarTodos()).thenReturn(List.of(exemplarResponse()));

        mockMvc.perform(get("/api/exemplares"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("DISPONIVEL"));
    }
    @Test
    @WithMockUser(roles = "USUARIO")
    void buscarPorIdDeveRetornar404QuandoNaoEncontrado() throws Exception {

        when(service.listarPorId(99L))
                .thenThrow(new ResourceNotFoundException("Exemplar não encontrado com id: 99"));

        mockMvc.perform(get("/api/exemplares/99"))
                .andExpect(status().isNotFound());
    }
    @Test
    void buscarPorLivroDeveSerPublico() throws Exception {

        when(service.listarPorLivro(1L)).thenReturn(List.of(exemplarResponse()));

        mockMvc.perform(get("/api/exemplares/livro/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].livroId").value(1L));
    }
    @Test
    void criarDeveRetornar401QuandoNaoAutenticado() throws Exception {

        mockMvc.perform(post("/api/exemplares")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exemplarRequestValido())))
                .andExpect(status().isUnauthorized());

        verify(service, never()).criar(any());
    }

}
