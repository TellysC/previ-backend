CREATE TABLE exames (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cliente_id UUID NOT NULL,
    nome VARCHAR(150) NOT NULL,
    data DATE NOT NULL,
    hora TIME,
    data_realizacao DATE,
    local VARCHAR(255),

    status VARCHAR(20) NOT NULL DEFAULT 'AGENDADO',
    observacao VARCHAR(255),

    CONSTRAINT fk_exame_cliente
        FOREIGN KEY (cliente_id)
        REFERENCES cliente(id),

    CONSTRAINT ck_status_exame
        CHECK ( status IN(
            'AGENDADO',
            'CONCLUIDO',
            'CANCELADO',
            'REAGENDADO'
        ))
);

CREATE INDEX idx_exame_cliente_data
        ON exames (cliente_id, data);