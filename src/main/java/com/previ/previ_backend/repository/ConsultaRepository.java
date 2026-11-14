
package com.previ.previ_backend.repository;

import com.previ.previ_backend.model.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConsultaRepository extends JpaRepository<Consulta, UUID> {

    List<Consulta> findByClienteId(UUID clienteId);

    Optional<Consulta> findByIdAndCliente_Id(
            UUID id,
            UUID clienteId
    );
}
