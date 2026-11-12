
package com.previ.previ_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public record ExameRequestDTO(

        @NotBlank
        @Size(max = 150)
        String nome,

        @NotNull
        LocalDate data,

        LocalTime hora,

        LocalDate dataRealizacao,

        @Size(max = 255)
        String local,

        @Size(max = 255)
        String observacao

) {}
