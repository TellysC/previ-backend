package com.previ.previ_backend.dto;

import jakarta.validation.constraints.Size;

public record AtualizarConteudoDTO(
        @Size(max = 200)
        String titulo,

        @Size(max = 500)
        String resumo,

        String texto,

        @Size(max = 100)
        String categoria
){}
