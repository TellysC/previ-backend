
package com.previ.previ_backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CicloMenstrualRequestDTO(

        @NotNull(message = "A data de início é obrigatória")
        LocalDate dataInicio,

        LocalDate dataFim,

        @Size(max = 255, message = "A observação deve ter no máximo 255 caracteres")
        String observacao

) {
}
