package com.previ.previ_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AlterarSenhaDTO(
        @NotBlank
        String senhaAtual,

        @NotBlank
        @Size(min = 8)
        String novaSenha
){}
