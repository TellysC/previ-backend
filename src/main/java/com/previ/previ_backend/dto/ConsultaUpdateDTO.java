
package com.previ.previ_backend.dto;

import com.previ.previ_backend.enums.StatusConsulta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public record ConsultaUpdateDTO(

        @NotBlank(message = "A especialidade é obrigatória")
        @Size(max = 150)
        String especialidade,

        @Size(max = 150)
        String profissional,

        @NotNull(message = "A data da consulta é obrigatória")
        LocalDate data,

        LocalTime hora,

        LocalDate dataRealizacao,

        @Size(max = 255)
        String local,

        @NotNull(message = "O status da consulta é obrigatório")
        StatusConsulta status,

        @Size(max = 255)
        String observacao

) {}
