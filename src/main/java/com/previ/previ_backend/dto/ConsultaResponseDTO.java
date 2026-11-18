
package com.previ.previ_backend.dto;

import com.previ.previ_backend.enums.StatusConsulta;
import com.previ.previ_backend.model.Consulta;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record ConsultaResponseDTO(
        UUID id,
        String especialidade,
        String profissional,
        LocalDate data,
        LocalTime hora,
        LocalDate dataRealizacao,
        String local,
        StatusConsulta status,
        String observacao,
        UUID consultaOrigemId
) {

    public static ConsultaResponseDTO fromEntity(Consulta consulta) {
        return new ConsultaResponseDTO(
                consulta.getId(),
                consulta.getEspecialidade(),
                consulta.getProfissional(),
                consulta.getData(),
                consulta.getHora(),
                consulta.getDataRealizacao(),
                consulta.getLocal(),
                consulta.getStatus(),
                consulta.getObservacao(),
                consulta.getConsultaOrigem() != null
                        ? consulta.getConsultaOrigem().getId()
                        : null
        );
    }
}
