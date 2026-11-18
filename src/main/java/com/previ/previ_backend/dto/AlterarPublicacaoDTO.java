package com.previ.previ_backend.dto;

import jakarta.validation.constraints.NotNull;

public record AlterarPublicacaoDTO(

        @NotNull(message = "Informe o status de publicação")
        Boolean publicado
){}
