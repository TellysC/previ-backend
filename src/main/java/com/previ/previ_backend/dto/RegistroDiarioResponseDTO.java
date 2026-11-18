
package com.previ.previ_backend.dto;

import com.previ.previ_backend.model.RegistroDiario;
import com.previ.previ_backend.model.Sintoma;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record RegistroDiarioResponseDTO(
        UUID id,
        LocalDate data,
        String observacao,
        String fluxo,
        String dor,
        String humor,
        Set<UUID> sintomasIds
) {

    public static RegistroDiarioResponseDTO fromEntity(
            RegistroDiario registro
    ) {
        Set<UUID> sintomasIds = registro.getSintomas()
                .stream()
                .map(Sintoma::getId)
                .collect(Collectors.toSet());

        return new RegistroDiarioResponseDTO(
                registro.getId(),
                registro.getData(),
                registro.getObservacao(),
                registro.getFluxo(),
                registro.getDor(),
                registro.getHumor(),
                sintomasIds
        );
    }
}
