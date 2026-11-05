CREATE TABLE registro_diario (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cliente_id UUID NOT NULL,
    data DATE NOT NULL,
    observacao VARCHAR(255),

    CONSTRAINT fk_registro_cliente
        FOREIGN KEY (cliente_id) REFERENCES cliente(id),

    CONSTRAINT uk_registro_cliente_data
        UNIQUE (cliente_id, data)
);

CREATE TABLE sintoma (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(100) NOT NULL UNIQUE,
    descricao VARCHAR(255)
);

CREATE TABLE registro_diario_sintoma (
    registro_diario_id UUID NOT NULL,
    sintoma_id UUID NOT NULL,

    PRIMARY KEY (registro_diario_id, sintoma_id),

    CONSTRAINT fk_registro_sintoma_registro
        FOREIGN KEY (registro_diario_id)
        REFERENCES registro_diario(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_registro_sintoma_sintoma
        FOREIGN KEY (sintoma_id)
        REFERENCES sintoma(id)
);