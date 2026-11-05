CREATE TABLE ciclo (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cliente_id UUID NOT NULL,
    data_inicio DATE NOT NULL,
    data_fim DATE,
    observacao VARCHAR(255),

    CONSTRAINT fk_ciclo_cliente REFERENCES cliente(id),

    CONSTRAINT ck_datas_ciclo
        CHECK ( data_fim IS NULL OR data_fim >= data_inicio)
);