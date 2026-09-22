package com.backend.Biblioteca.integration;

import com.backend.Biblioteca.domain.model.Autor;
import com.backend.Biblioteca.infrastructure.repository.AutorRepository;
import com.backend.Biblioteca.infrastructure.config.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("it")
public class TestcontainersSmokeIT {

    @Autowired
    private AutorRepository autorRepository;

    @Test
    void contextloadsAndFlywayMigrationAppliedSuccessfully(){
        assertNotNull(autorRepository);
    }
    @Test
    void deveSalvarEBuscarAutorNoMysqlReal() {

        Autor autor = new Autor();
        autor.setNome("Machado de Assis");
        autor.setBiografia("Escritor brasileiro, um dos maiores nomes da literatura nacional.");
        autor.setAnoNascimento(1839);
        autor.setNacionalidade("Brasileira");

        Autor salvo = autorRepository.save(autor);

        assertNotNull(salvo.getId());

        Autor encontrado = autorRepository.findById(salvo.getId()).orElseThrow();
        assertEquals("Machado de Assis", encontrado.getNome());
        assertEquals("Brasileira", encontrado.getNacionalidade());
    }
}
