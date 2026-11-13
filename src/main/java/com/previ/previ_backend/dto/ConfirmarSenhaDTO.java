package com.previ.previ_backend.dto;

import jakarta.validation.constraints.NotBlank;

public record ConfirmarSenhaDTO(

        @NotBlank
        String senhaAtual
){}
