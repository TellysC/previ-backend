
package com.previ.previ_backend.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public record ConsultaRetornoRequestDTO(

        @NotNull(message = "A data do retorno é obrigatória")
        @FutureOrPresent(message = "A data do retorno não pode estar no passado")
        LocalDate data,

        LocalTime hora,

        @Size(max = 150)
        String profissional,

        @Size(max = 255)
        String local,

        @Size(max = 255)
        String observacao

) {}
