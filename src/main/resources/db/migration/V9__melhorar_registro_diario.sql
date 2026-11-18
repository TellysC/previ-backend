-- Intensidade do fluxo menstrual
ALTER TABLE registro_diario
    ADD COLUMN fluxo VARCHAR(20);

-- Intensidade da dor
ALTER TABLE registro_diario
    ADD COLUMN dor VARCHAR(20);

-- Humor registrado
ALTER TABLE registro_diario
    ADD COLUMN humor VARCHAR(20);

-- Valores permitidos para o fluxo
ALTER TABLE registro_diario
    ADD CONSTRAINT ck_registro_fluxo
        CHECK (
            fluxo IS NULL OR
            fluxo IN ('LEVE', 'MODERADO', 'INTENSO')
            );

-- Valores permitidos para a dor
ALTER TABLE registro_diario
    ADD CONSTRAINT ck_registro_dor
        CHECK (
            dor IS NULL OR
            dor IN ('LEVE', 'MODERADA', 'INTENSA')
            );

-- Valores permitidos para o humor
ALTER TABLE registro_diario
    ADD CONSTRAINT ck_registro_humor
        CHECK (
            humor IS NULL OR
            humor IN (
                      'FELIZ',
                      'TRISTE',
                      'ANSIOSA',
                      'IRRITADA',
                      'TRANQUILA'
                )
            );
