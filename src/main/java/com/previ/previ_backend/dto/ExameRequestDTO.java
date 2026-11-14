
package com.previ.previ_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public record ExameRequestDTO(

        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 150)
        String nome,

        @NotNull(message = "A data é obrigatório")
        LocalDate data,

        LocalTime hora,

        @Size(max = 255)
        String local,

        @Size(max = 255)
        String observacao

) {}
