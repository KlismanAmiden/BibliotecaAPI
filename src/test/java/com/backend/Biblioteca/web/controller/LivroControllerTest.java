package com.backend.Biblioteca.web.controller;

import com.backend.Biblioteca.application.dto.request.LivroRequestDTO;
import com.backend.Biblioteca.application.dto.response.AutorResponseDTO;
import com.backend.Biblioteca.application.dto.response.GeneroResponseDTO;
import com.backend.Biblioteca.application.dto.response.LivroResponseDTO;
import com.backend.Biblioteca.application.service.LivroService;
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
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LivroController.class)
@Import(SecurityConfig.class)
public class LivroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LivroService service;

    @MockitoBean
    private JwtUtil jwtUtil;

    private LivroResponseDTO livroResponse() {
        return new LivroResponseDTO(
                1L, "Dom Casmurro", "978-85-359-0277-0", 1899, "Um clássico da literatura brasileira", "Editora XPTO",
                Set.of(new AutorResponseDTO(1L, "Machado de Assis", "Escritor brasileiro", 1839, "Brasileira")),
                Set.of(new GeneroResponseDTO(1L, "Romance", "Ficção narrativa"))
        );
    }

    private LivroRequestDTO livroRequestValido() {
        return new LivroRequestDTO(
                "Dom Casmurro", "978-85-359-0277-0", 1899, "Um clássico da literatura brasileira", "Editora XPTO",
                Set.of(1L), Set.of(1L)
        );
    }
    @Test
    void listarTodosDeveSerPublico() throws Exception {

        when(service.listarTodos()).thenReturn(List.of(livroResponse()));

        mockMvc.perform(get("/api/livros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Dom Casmurro"));
    }
    @Test
    void listarPorGeneroDeveSerPublico() throws Exception {

        when(service.listarPorGenero(1L)).thenReturn(List.of(livroResponse()));

        mockMvc.perform(get("/api/livros/genero/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].generos[0].nome").value("Romance"));
    }
    @Test
    void listarPorAutorDeveSerPublico() throws Exception {

        when(service.listarPorAutor(1L)).thenReturn(List.of(livroResponse()));

        mockMvc.perform(get("/api/livros/autor/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].autores[0].nome").value("Machado de Assis"));
    }
    @Test
    void buscarPorIdDeveSerPublico() throws Exception {

        when(service.buscarPorId(1L)).thenReturn(livroResponse());

        mockMvc.perform(get("/api/livros/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn").value("978-85-359-0277-0"));
    }
    @Test
    void buscarPorIdDeveRetornar404QuandoNaoEncontrado() throws Exception {

        when(service.buscarPorId(99L))
                .thenThrow(new ResourceNotFoundException("Livro não encontrado com id: 99"));

        mockMvc.perform(get("/api/livros/99"))
                .andExpect(status().isNotFound());
    }
    @Test
    void criarDeveRetornar401QuandoNaoAutenticado() throws Exception {

        mockMvc.perform(post("/api/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(livroRequestValido())))
                .andExpect(status().isUnauthorized());

        verify(service, never()).criar(any());
    }
    @Test
    @WithMockUser(roles = "USUARIO")
    void criarDeveRetornar403QuandoRoleNaoAutorizada() throws Exception {

        mockMvc.perform(post("/api/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(livroRequestValido())))
                .andExpect(status().isForbidden());

        verify(service, never()).criar(any());
    }
    @Test
    @WithMockUser(roles = "BIBLIOTECARIO")
    void criarDeveRetornar201QuandoAutorizado() throws Exception {

        when(service.criar(any(LivroRequestDTO.class))).thenReturn(livroResponse());

        mockMvc.perform(post("/api/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(livroRequestValido())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Dom Casmurro"));
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void criarDeveRetornar400QuandoAutoresOuGenerosVazios() throws Exception {

        LivroRequestDTO dtoInvalido = new LivroRequestDTO(
                "Dom Casmurro", "978-85-359-0277-0", 1899, "desc", "Editora XPTO",
                Set.of(), Set.of()
        );

        mockMvc.perform(post("/api/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest());

        verify(service, never()).criar(any());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void criarDeveRetornar400QuandoAnoForaDoIntervalo() throws Exception {

        LivroRequestDTO dtoInvalido = new LivroRequestDTO(
                "Dom Casmurro", "978-85-359-0277-0", 2100, "desc", "Editora XPTO",
                Set.of(1L), Set.of(1L)
        );

        mockMvc.perform(post("/api/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest());

        verify(service, never()).criar(any());
    }
    @Test
    @WithMockUser(roles = "BIBLIOTECARIO")
    void atualizarDeveRetornar200QuandoAutorizado() throws Exception {

        when(service.atualizar(eq(1L), any(LivroRequestDTO.class))).thenReturn(livroResponse());

        mockMvc.perform(put("/api/livros/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(livroRequestValido())))
                .andExpect(status().isOk());
    }
}
