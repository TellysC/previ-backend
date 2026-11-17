
package com.previ.previ_backend.repository;

import com.previ.previ_backend.model.CicloMenstrual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CicloMenstrualRepository
        extends JpaRepository<CicloMenstrual, UUID> {

    List<CicloMenstrual> findByClienteIdOrderByDataInicioDesc(
            UUID clienteId
    );

    Optional<CicloMenstrual> findByIdAndCliente_Id(
            UUID cicloId,
            UUID clienteId
    );

    @Query("""
    SELECT COUNT(c) > 0
    FROM CicloMenstrual c
    WHERE c.cliente.id = :clienteId
      AND (:cicloIdIgnorado IS NULL OR c.id <> :cicloIdIgnorado)
      AND c.dataInicio <= :dataFim
      AND COALESCE(c.dataFim, CURRENT_DATE) >= :dataInicio
    """)
    boolean existeSobreposicao(
            @Param("clienteId") UUID clienteId,
            @Param("cicloIdIgnorado") UUID cicloIdIgnorado,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim
    );

}

