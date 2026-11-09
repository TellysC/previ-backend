package com.previ.previ_backend.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.Set;

public record ClienteCadastroDTO(

        @NotBlank
        @Size(min = 3, max = 255)
        String nome,

        @NotBlank
        @Email
        String email,

        @NotBlank
        @Size(min = 8)
        String senha,

        @NotNull
        @Past
        LocalDate dataNascimento,

        @NotNull
        @Size(min = 1)
        Set<
                @NotBlank
                String> telefones
) {}
