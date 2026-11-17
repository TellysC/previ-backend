
CREATE TABLE vacinas (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cliente_id UUID NOT NULL,

    nome VARCHAR(150) NOT NULL,
    dose INTEGER NOT NULL,
    data_aplicacao DATE NOT NULL,
    proxima_dose DATE,
    local_aplicacao VARCHAR(255),
    observacoes TEXT,

    CONSTRAINT ck_dose_vacina
        CHECK (dose > 0),

    CONSTRAINT fk_vacinas_cliente
        FOREIGN KEY (cliente_id)
            REFERENCES cliente(id)
);

CREATE INDEX idx_vacinas_cliente_data
    ON vacinas (cliente_id, data_aplicacao);
