-- Permitir vincular um retorno à consulta original
ALTER TABLE consultas
    ADD COLUMN consulta_origem_id UUID;

-- Criar relacionamento com a consulta original
ALTER TABLE consultas
    ADD CONSTRAINT fk_consulta_origem
        FOREIGN KEY (consulta_origem_id)
            REFERENCES consultas(id);

-- Melhorar a busca de retornos
CREATE INDEX idx_consulta_origem
    ON consultas(consulta_origem_id);
