package com.backend.Biblioteca.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String BEARER_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI bibliotecaOpenAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("BibliotecaAPI")
                        .description("API REST para gestão de biblioteca: livros, autores, gêneros, "
                                + "exemplares, usuários e empréstimos, com autenticação via JWT.")

                        .version("v1")
                        .contact(new Contact().email("Wkklisman08@Gmail.com")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME))
                .components(new Components()
                        .addSecuritySchemes(BEARER_SCHEME,new SecurityScheme()
                                .name(BEARER_SCHEME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Cole aqui o token retornado por POST /api/auth/login "
                                        + "(sem o prefixo \"Bearer \", o Swagger adiciona sozinho).")));

    }
}
