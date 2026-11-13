package com.previ.previ_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AlterarEmailDTO(
        @NotBlank
        @Email
        String novoEmail,

        @NotBlank
        String senhaAtual
){}
