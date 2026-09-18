package com.backend.Biblioteca.application.service;

import com.backend.Biblioteca.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.util.ReflectionTestUtils;

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
}
