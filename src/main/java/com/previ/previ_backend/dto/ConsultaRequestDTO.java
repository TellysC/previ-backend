
package com.previ.previ_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public record ConsultaRequestDTO(

        @NotBlank(message = "A especialidade é obrigatória")
        @Size(max = 150)
        String especialidade,

        @Size(max = 150)
        String profissional,

        @NotNull(message = "A data da consulta é obrigatória")
        LocalDate data,

        LocalTime hora,

        @Size(max = 255)
        String local,

        @Size(max = 255)
        String observacao

) {}
