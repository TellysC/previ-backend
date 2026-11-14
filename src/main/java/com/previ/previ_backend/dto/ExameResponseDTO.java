
package com.previ.previ_backend.dto;

import com.previ.previ_backend.enums.StatusExame;
import com.previ.previ_backend.model.Exame;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record ExameResponseDTO(
        UUID id,
        String nome,
        LocalDate data,
        LocalTime hora,
        LocalDate dataRealizacao,
        String local,
        StatusExame status,
        String observacao
) {
    public static ExameResponseDTO fromEntity(Exame exame) {
        return new ExameResponseDTO(
                exame.getId(),
                exame.getNome(),
                exame.getData(),
                exame.getHora(),
                exame.getDataRealizacao(),
                exame.getLocal(),
                exame.getStatus(),
                exame.getObservacao()
        );
    }
}
