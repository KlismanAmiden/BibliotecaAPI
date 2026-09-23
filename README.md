# 📚 BibliotecaAPI

API REST para gerenciamento de biblioteca, desenvolvida em **Java 25** com **Spring Boot 4.1.1**. Projeto de portfólio com autenticação JWT, documentação OpenAPI/Swagger, containerização com Docker e pipeline de CI/CD.

🔗 **Swagger UI (deploy):** [bibliotecaapi-latest.onrender.com/swagger-ui/index.html](https://bibliotecaapi-latest.onrender.com/swagger-ui/index.html)

> ⚠️ A API está hospedada no plano gratuito do Render — o primeiro request após um período de inatividade pode demorar alguns segundos.

---

## ✨ Funcionalidades

- Cadastro e consulta de **livros**, **autores**, **gêneros** e **exemplares** físicos
- Controle de **status dos exemplares** (`DISPONIVEL`, `EMPRESTADO`, `INDISPONIVEL`)
- Fluxo completo de **empréstimos**: criação, devolução e consulta por usuário
- Regras de negócio de biblioteca:
  - Máximo de **3 empréstimos ativos simultâneos** por usuário
  - Novo empréstimo **bloqueado** se o usuário tiver empréstimo em atraso
  - Cálculo automático de **multa** (R$ 2,00/dia de atraso)
- **Autenticação e autorização via JWT**, com papéis (`ADMIN`, `BIBLIOTECARIO`, `USUARIO`)
- Criação automática de um usuário **admin** na primeira subida da aplicação
- Documentação interativa via **Swagger / OpenAPI**
- **Testes unitários e de integração** (JUnit 5, Mockito, Testcontainers)
- **CI** (build e testes a cada push/PR) e **CD** (build e publicação da imagem Docker) via GitHub Actions

---

## 🛠️ Stack técnica

| Categoria | Tecnologias |
|---|---|
| Linguagem / Runtime | Java 25 |
| Framework | Spring Boot 4.1.1 (Web, Data JPA, Security, Validation, Actuator) |
| Banco de dados | MySQL 8 (produção/dev), H2 (testes) |
| Migrações | Flyway |
| Autenticação | Spring Security + JWT (jjwt 0.13.0) |
| Documentação | springdoc-openapi (Swagger UI) 2.8.5 |
| Testes | JUnit 5, Mockito, Spring Boot Test, Testcontainers (MySQL) |
| Build | Maven (wrapper incluso) |
| Infra | Docker, Docker Compose |
| CI/CD | GitHub Actions (CI de testes + CD de imagem Docker) |
| Deploy | Render |

---

## 🏗️ Arquitetura

O projeto segue uma separação de camadas inspirada em Clean Architecture:

```
com.backend.Biblioteca
├── domain
│   ├── model          # Entidades de domínio (Livro, Autor, Genero, Exemplar, Emprestimo, Usuario)
│   └── enums           # Role, StatusEmprestimo, StatusExemplar
├── application
│   ├── service          # Regras de negócio (LivroService, EmprestimoService, AuthService, ...)
│   └── dto
│       ├── request       # DTOs de entrada
│       └── response       # DTOs de saída
├── infrastructure
│   ├── repository       # Repositórios Spring Data JPA
│   ├── security          # JwtUtil, JwtAuthFilter, AuthenticatedUser
│   └── config             # SecurityConfig, OpenApiConfig, AdminSeeder
└── web
    ├── controller        # Controllers REST
    └── exception          # Exceções e handler global
```

### Modelo de dados (resumo)

- Um **Livro** pode ter vários **Exemplares** físicos (relação 1:N)
- **Livro** se relaciona com **Autor** e **Gênero** em N:N
- Um **Empréstimo** vincula um **Usuario** a um **Exemplar**
- **Usuario** tem um conjunto de papéis (`Role`), em vez de entidades separadas por tipo de usuário

---

## 🔐 Autenticação e autorização

A API usa **JWT** via header `Authorization: Bearer <token>`, obtido em `POST /api/auth/login`.

Papéis disponíveis: `ADMIN`, `BIBLIOTECARIO`, `USUARIO`.

Endpoints públicos (sem autenticação):
- `POST /api/auth/login`
- `POST /api/usuarios` (cadastro/signup)
- `GET /api/livros/**`, `GET /api/autores/**`, `GET /api/generos/**`
- `GET /api/exemplares/livro/{livroId}`
- Swagger UI e OpenAPI docs

Todos os demais endpoints exigem autenticação; vários exigem papel `ADMIN` e/ou `BIBLIOTECARIO` (ver tabela de endpoints abaixo).

Na primeira subida da aplicação, um usuário admin é criado automaticamente a partir das variáveis de ambiente `ADMIN_*` (ver seção de configuração).

---

## 📡 Endpoints principais

### Autenticação
| Método | Rota | Acesso |
|---|---|---|
| POST | `/api/auth/login` | Público |

### Usuários
| Método | Rota | Acesso |
|---|---|---|
| GET | `/api/usuarios` | ADMIN, BIBLIOTECARIO |
| GET | `/api/usuarios/{id}` | Autenticado (próprio usuário ou ADMIN) |
| POST | `/api/usuarios` | Público (signup) |
| PUT | `/api/usuarios/{id}` | Autenticado (próprio usuário ou ADMIN) |
| DELETE | `/api/usuarios/{id}` | Autenticado (próprio usuário ou ADMIN) |

### Livros
| Método | Rota | Acesso |
|---|---|---|
| GET | `/api/livros` | Público |
| GET | `/api/livros/{id}` | Público |
| GET | `/api/livros/genero/{generoId}` | Público |
| GET | `/api/livros/autor/{autorId}` | Público |
| POST | `/api/livros` | ADMIN, BIBLIOTECARIO |
| PUT | `/api/livros/{id}` | ADMIN, BIBLIOTECARIO |
| DELETE | `/api/livros/{id}` | ADMIN |

### Autores
| Método | Rota | Acesso |
|---|---|---|
| GET | `/api/autores` | Público |
| GET | `/api/autores/{id}` | Público |
| POST | `/api/autores` | ADMIN, BIBLIOTECARIO |
| PUT | `/api/autores/{id}` | ADMIN, BIBLIOTECARIO |
| DELETE | `/api/autores/{id}` | ADMIN |

### Gêneros
| Método | Rota | Acesso |
|---|---|---|
| GET | `/api/generos` | Público |
| GET | `/api/generos/{id}` | Público |
| POST | `/api/generos` | ADMIN, BIBLIOTECARIO |
| PUT | `/api/generos/{id}` | ADMIN, BIBLIOTECARIO |
| DELETE | `/api/generos/{id}` | ADMIN |

### Exemplares
| Método | Rota | Acesso |
|---|---|---|
| GET | `/api/exemplares` | Autenticado |
| GET | `/api/exemplares/{id}` | Autenticado |
| GET | `/api/exemplares/livro/{livroId}` | Público |
| POST | `/api/exemplares` | ADMIN, BIBLIOTECARIO |
| PATCH | `/api/exemplares/{id}/status` | ADMIN, BIBLIOTECARIO |
| DELETE | `/api/exemplares/{id}` | ADMIN |

### Empréstimos
| Método | Rota | Acesso |
|---|---|---|
| GET | `/api/emprestimos` | ADMIN, BIBLIOTECARIO |
| GET | `/api/emprestimos/{id}` | ADMIN, BIBLIOTECARIO |
| GET | `/api/emprestimos/usuario/{usuarioId}` | Autenticado |
| POST | `/api/emprestimos` | Autenticado (usuário cria para si; ADMIN/BIBLIOTECARIO para qualquer um) |
| PATCH | `/api/emprestimos/{id}/devolver` | ADMIN, BIBLIOTECARIO |

Documentação completa e interativa de todos os endpoints, DTOs e schemas está disponível no [Swagger UI](https://bibliotecaapi-latest.onrender.com/swagger-ui/index.html).

---

## 🚀 Como rodar localmente

### Pré-requisitos
- Java 25
- Docker e Docker Compose

### 1. Clonar o repositório
```bash
git clone https://github.com/KlismanAmiden/BibliotecaAPI.git
cd BibliotecaAPI
```

### 2. Configurar variáveis de ambiente
Copie o arquivo de exemplo e ajuste os valores:
```bash
cp .env.example .env
```

Variáveis principais:

| Variável | Descrição |
|---|---|
| `DB_USERNAME` / `DB_PASSWORD` | Credenciais do banco MySQL |
| `MYSQL_ROOT_PASSWORD` | Senha root do MySQL (Docker Compose) |
| `JWT_SECRET` | Chave secreta usada para assinar os tokens JWT |
| `JWT_EXPIRATION_MS` | Tempo de expiração do token, em milissegundos |
| `ADMIN_NOME`, `ADMIN_EMAIL`, `ADMIN_SENHA`, `ADMIN_TELEFONE` | Dados do usuário admin criado automaticamente na primeira subida |
| `SERVER_PORT` | Porta da aplicação (padrão `8900`) |

### 3. Subir com Docker Compose
```bash
docker compose up --build
```
A API sobe em `http://localhost:8900` e o MySQL fica exposto em `localhost:3307`.

### 4. Rodar sem Docker (opcional)
Com um MySQL local rodando e as variáveis de ambiente exportadas:
```bash
./mvnw spring-boot:run
```

### Acessar a documentação
Com a aplicação no ar:
- Swagger UI: `http://localhost:8900/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8900/v3/api-docs`

---

## ✅ Testes

```bash
./mvnw test
```

O projeto inclui:
- **Testes unitários** dos services (`LivroService`, `AutorService`, `GeneroService`, `EmprestimoService`, `ExemplarService`, `UsuarioService`, `AuthService`, `JwtUtil`) com JUnit 5 + Mockito, sem contexto Spring
- **Testes de controller** com `@WebMvcTest`
- **Testes de integração** (`AuthFlowIT`, `EmprestimoFlowIT`) usando **Testcontainers** com MySQL real

---

## 🔄 CI/CD

- **CI** (`.github/workflows/CI.yml`): a cada push/PR na `main`, roda `./mvnw test`
- **CD** (`.github/workflows/CD.yml`): a cada push na `main`, builda e publica a imagem Docker no Docker Hub (`bibliotecaapi:latest` e `bibliotecaapi:<sha>`)

---

## 👤 Autor

Desenvolvido por **Klisman Almeida Santos** — [LinkedIn](https://www.linkedin.com/in/klisman-almeida-santos-352406268/)
