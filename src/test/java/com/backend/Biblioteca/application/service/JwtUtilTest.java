package com.backend.Biblioteca.application.service;

import com.backend.Biblioteca.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@RequiredArgsConstructor
public class JwtUtilTest {

    private static final String SECRET = "chave-secreta-de-teste-com-pelo-menos-32-bytes-para-hs256";
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", SECRET);
        ReflectionTestUtils.setField(jwtUtil, "expirationMs", 3600000); // 1 hora
    }
    @Test
    void deveGerarTokenNaoNuloENaoVazio() {

        String token = jwtUtil.generateToken("klisman@email.com", 1L, "USUARIO");

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertEquals(3, token.split("\\.").length);
    }
    @Test
    void deveConsiderarTokenValidoQuandoDentroDoPrazo() {

        String token = jwtUtil.generateToken("klisman@email.com", 1L, "USUARIO");

        assertTrue(jwtUtil.isTokenValido(token));
    }
    @Test
    void deveExtrairEmailCorretamenteDoToken() {

        String token = jwtUtil.generateToken("klisman@email.com", 1L, "USUARIO");

        assertEquals("klisman@email.com", jwtUtil.extrairEmail(token));
    }
    @Test
    void deveExtrairRoleCorretamenteDoToken() {

        String token = jwtUtil.generateToken("klisman@email.com", 1L, "ADMIN");

        assertEquals("ADMIN", jwtUtil.extrairRole(token));
    }
    @Test
    void deveConsiderarTokenInvalidoQuandoExpirado() {

        ReflectionTestUtils.setField(jwtUtil, "expirationMs", -1000);

        String tokenExpirado = jwtUtil.generateToken("klisman@email.com", 1L, "USUARIO");

        assertFalse(jwtUtil.isTokenValido(tokenExpirado));
    }
    @Test
    void deveConsiderarTokenInvalidoQuandoMalformado() {

        assertFalse(jwtUtil.isTokenValido("token.invalido.aqui"));
    }
    @Test
    void deveConsiderarTokenInvalidoQuandoAssinadoComOutraChave() {

        String token = jwtUtil.generateToken("klisman@email.com", 1L, "USUARIO");

        JwtUtil outroJwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(outroJwtUtil, "secret", "outra-chave-secreta-totalmente-diferente-32-bytes");
        ReflectionTestUtils.setField(outroJwtUtil, "expirationMs", 3600000);

        assertFalse(outroJwtUtil.isTokenValido(token));
    }
}
