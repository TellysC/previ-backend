
package com.previ.previ_backend.dto;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CicloMenstrualUpdateDTO(

        LocalDate dataInicio,

        LocalDate dataFim,

        @Size(max = 255, message = "A observação deve ter no máximo 255 caracteres")
        String observacao

) {
}
