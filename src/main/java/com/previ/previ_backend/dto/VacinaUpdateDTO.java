
package com.previ.previ_backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record VacinaUpdateDTO(

        @NotBlank(message = "O nome da vacina é obrigatório")
        @Size(max = 150)
        String nome,

        @NotNull(message = "A dose é obrigatória")
        @Min(value = 1, message = "A dose deve ser maior que zero")
        Integer dose,

        @NotNull(message = "A data de aplicação é obrigatória")
        LocalDate dataAplicacao,

        LocalDate proximaDose,

        @Size(max = 255)
        String localAplicacao,

        String observacoes

) {}
