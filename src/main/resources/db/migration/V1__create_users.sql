CREATE TABLE usuarios (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR (255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    create_in TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE administrador (
    id UUID PRIMARY KEY,
    CONSTRAINT fk_administrador_usuario
        FOREIGN KEY (id) REFERENCES usuarios (id)
);

CREATE TABLE cliente (
    id UUID PRIMARY KEY,
    data_nasc DATE,
    CONSTRAINT fk_cliente_usuario
        FOREIGN KEY (id) REFERENCES usuarios (id)
);

CREATE TABLE cliente_telefone (
    cliente_id UUID NOT NULL,
    telefone VARCHAR(15) NOT NULL,

    PRIMARY KEY (cliente_id, telefone),

    CONSTRAINT fk_telefone_cliente
        FOREIGN KEY (cliente_id)
        REFERENCES cliente(id)
        ON DELETE CASCADE
);