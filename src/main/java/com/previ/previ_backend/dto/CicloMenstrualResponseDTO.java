
package com.previ.previ_backend.dto;

import java.time.LocalDate;
import java.util.UUID;

public record CicloMenstrualResponseDTO(
        UUID id,
        LocalDate dataInicio,
        LocalDate dataFim,
        String observacao
) {
}
