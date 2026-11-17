
package com.previ.previ_backend.repository;

import com.previ.previ_backend.model.Vacina;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VacinaRepository extends JpaRepository<Vacina, UUID> {

    List<Vacina> findByClienteId(UUID clienteId);

    Optional<Vacina> findByIdAndCliente_Id(
            UUID vacinaId,
            UUID clienteId
    );
}
