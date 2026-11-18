
INSERT INTO sintoma (nome, descricao)
VALUES
    ('Cólica', 'Dor ou desconforto abdominal'),
    ('Dor de cabeça', 'Dor na região da cabeça'),
    ('Náusea', 'Sensação de enjoo'),
    ('Inchaço', 'Sensação de inchaço corporal'),
    ('Sensibilidade nos seios', 'Sensibilidade ou desconforto mamário'),
    ('Fadiga', 'Sensação de cansaço'),
    ('Tontura', 'Sensação de tontura'),
    ('Dor lombar', 'Dor na região inferior das costas'),
    ('Acne', 'Aparecimento de espinhas'),
    ('Alteração de apetite', 'Mudanças no apetite')
    ON CONFLICT (nome) DO NOTHING;
