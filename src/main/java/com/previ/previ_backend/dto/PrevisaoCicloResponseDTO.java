
package com.previ.previ_backend.dto;

import java.time.LocalDate;

public record PrevisaoCicloResponseDTO(
        Double mediaDuracaoCiclo,
        LocalDate ultimaMenstruacao,
        LocalDate proximaMenstruacaoEstimada,
        Integer quantidadeCiclosAnalisados,
        Integer variacaoDuracaoCiclo,
        Boolean previsaoUltrapassada,
        LocalDate ovulacaoEstimada,
        LocalDate inicioJanelaFertilEstimada,
        LocalDate fimJanelaFertilEstimada,
        String aviso
) {}
