
package com.previ.previ_backend.dto;

import com.previ.previ_backend.model.Sintoma;

import java.util.UUID;

public record SintomaResponseDTO(
        UUID id,
        String nome,
        String descricao
) {

    public static SintomaResponseDTO fromEntity(Sintoma sintoma) {
        return new SintomaResponseDTO(
                sintoma.getId(),
                sintoma.getNome(),
                sintoma.getDescricao()
        );
    }
}
