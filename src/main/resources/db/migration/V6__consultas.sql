
CREATE TABLE consultas (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cliente_id UUID NOT NULL,

    especialidade VARCHAR(150) NOT NULL,
    profissional VARCHAR(150),
    data DATE NOT NULL,
    hora TIME,
    data_realizacao DATE,
    local VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'AGENDADA',
    observacao VARCHAR(255),

    CONSTRAINT ck_status_consulta
        CHECK (status IN (
            'AGENDADA',
            'CONCLUIDA',
            'CANCELADA',
            'REAGENDADA'
    )),

    CONSTRAINT fk_consultas_cliente
        FOREIGN KEY (cliente_id)
            REFERENCES cliente(id)
);

CREATE INDEX idx_consulta_cliente_data
    ON consultas (cliente_id, data);
