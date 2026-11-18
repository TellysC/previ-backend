CREATE TABLE conteudos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    titulo VARCHAR(200) NOT NULL,
    resumo VARCHAR(500),
    texto TEXT NOT NULL,
    categoria VARCHAR(100) NOT NULL,
    publicado BOOLEAN NOT NULL DEFAULT FALSE,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_conteudos_publicado
    ON conteudos(publicado);

CREATE INDEX idx_conteudos_categoria
    ON conteudos(categoria);