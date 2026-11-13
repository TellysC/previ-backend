package com.previ.previ_backend.dto;

import jakarta.validation.constraints.Past;

import java.time.LocalDate;
import java.util.Set;

public record AtualizarPerfilDTO(
        String nome,
        @Past
        LocalDate dataNasc,

        Set<String> telefones
){}
