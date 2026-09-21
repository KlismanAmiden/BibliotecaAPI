
CREATE TABLE `livros` (
`id` bigint NOT NULL AUTO_INCREMENT,
`titulo` varchar(255) NOT NULL,
`isbn` varchar(255) NOT NULL,
`ano_publicado` int NOT NULL,
`descricao` text NOT NULL,
`editora` varchar(255) NOT NULL,
PRIMARY KEY (`id`),
UNIQUE KEY `uk_livros_isbn` (`isbn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `autores` (
`id` bigint NOT NULL AUTO_INCREMENT,
`nome` varchar(255) NOT NULL,
`biografia` text NOT NULL,
`ano_nascimento` int NOT NULL,
`nacionalidade` varchar(255) NOT NULL,
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `generos` (
`id` bigint NOT NULL AUTO_INCREMENT,
`nome` varchar(255) NOT NULL,
`descricao` text NOT NULL,
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `exemplares` (
`id` bigint NOT NULL AUTO_INCREMENT,
`livro_id` bigint NOT NULL,
`status` enum('DISPONIVEL','EMPRESTADO','INDISPONIVEL') NOT NULL,
PRIMARY KEY (`id`),
KEY `fk_exemplares_livro` (`livro_id`),
CONSTRAINT `fk_exemplares_livro` FOREIGN KEY (`livro_id`) REFERENCES `livros` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `livros_autores` (
`livro_id` bigint NOT NULL,
`autor_id` bigint NOT NULL,
PRIMARY KEY (`livro_id`,`autor_id`),
KEY `fk_livros_autores_autor` (`autor_id`),
CONSTRAINT `fk_livros_autores_livro` FOREIGN KEY (`livro_id`) REFERENCES `livros` (`id`),
CONSTRAINT `fk_livros_autores_autor` FOREIGN KEY (`autor_id`) REFERENCES `autores` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `livros_generos` (
`livro_id` bigint NOT NULL,
`genero_id` bigint NOT NULL,
PRIMARY KEY (`livro_id`,`genero_id`),
KEY `fk_livros_generos_genero` (`genero_id`),
CONSTRAINT `fk_livros_generos_livro` FOREIGN KEY (`livro_id`) REFERENCES `livros` (`id`),
CONSTRAINT `fk_livros_generos_genero` FOREIGN KEY (`genero_id`) REFERENCES `generos` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `usuarios` (
`id` bigint NOT NULL AUTO_INCREMENT,
`nome` varchar(255) NOT NULL,
`email` varchar(255) NOT NULL,
`senha` varchar(255) NOT NULL,
`telefone` varchar(255) NOT NULL,
`data_cadastro` datetime(6) NOT NULL,
`ativo` bit(1) NOT NULL,
`role` enum('ADMIN','BIBLIOTECARIO','USUARIO') NOT NULL,
PRIMARY KEY (`id`),
UNIQUE KEY `uk_usuarios_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `emprestimos` (
`id` bigint NOT NULL AUTO_INCREMENT,
`usuario_id` bigint NOT NULL,
`data_emprestimo` datetime(6) NOT NULL,
`data_prevista_devolucao` datetime(6) NOT NULL,
`data_devolucao` datetime(6) DEFAULT NULL,
`status` enum('ATIVO','ATRASADO','CANCELADO','DEVOLVIDO') NOT NULL,
`multa` decimal(38,2) DEFAULT NULL,
PRIMARY KEY (`id`),
KEY `fk_emprestimos_usuario` (`usuario_id`),
CONSTRAINT `fk_emprestimos_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `emprestimo-exemplar` (
`emprestimoid` bigint NOT NULL,
`exemplar_id` bigint NOT NULL,
PRIMARY KEY (`emprestimoid`,`exemplar_id`),
KEY `fk_emprestimo_exemplar_exemplar` (`exemplar_id`),
CONSTRAINT `fk_emprestimo_exemplar_emprestimo` FOREIGN KEY (`emprestimoid`) REFERENCES `emprestimos` (`id`),
CONSTRAINT `fk_emprestimo_exemplar_exemplar` FOREIGN KEY (`exemplar_id`) REFERENCES `exemplares` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;