
package com.previ.previ_backend.dto;

import java.time.LocalDate;
import java.util.UUID;

public record VacinaResponseDTO(

        UUID id,
        String nome,
        Integer dose,
        LocalDate dataAplicacao,
        LocalDate proximaDose,
        String localAplicacao,
        String observacoes

) {}
