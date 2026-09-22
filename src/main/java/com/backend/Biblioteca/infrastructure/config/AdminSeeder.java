package com.backend.Biblioteca.infrastructure.config;

import com.backend.Biblioteca.domain.enums.Role;
import com.backend.Biblioteca.domain.model.Usuario;
import com.backend.Biblioteca.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminSeeder.class);

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.nome}")
    private String adminNome;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.senha}")
    private String adminSenha;

    @Value("${app.admin.telefone}")
    private String adminTelefone;

    @Override
    public void run(String... args) {
        if (repository.existsByEmail(adminEmail)) {
            return;
        }

        Usuario admin = new Usuario();
        admin.setNome(adminNome);
        admin.setEmail(adminEmail);
        admin.setSenha(passwordEncoder.encode(adminSenha));
        admin.setTelefone(adminTelefone);
        admin.setDataCadastro(LocalDateTime.now());
        admin.setAtivo(true);
        admin.setRole(Role.ADMIN);

        repository.save(admin);
        LOGGER.info("Usuário ADMIN inicial criado com e-mail: {}", adminEmail);
    }
}
