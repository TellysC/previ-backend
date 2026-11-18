package com.previ.previ_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CriarConteudoDTO(

        @NotBlank(message = "O título é obrigatório")
        @Size(max = 200)
        String titulo,

        @Size(max = 500)
        String resumo,

        @NotBlank(message = "O texto é obrigatório")
        String texto,

        @NotBlank(message = "A categoria é obrigatória")
        @Size(max = 100)
        String categoria
){}
