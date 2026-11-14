
package com.previ.previ_backend.repository;

import com.previ.previ_backend.model.Exame;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExameRepository extends JpaRepository<Exame, UUID> {

    List<Exame> findByClienteId(UUID clienteId);

    Optional<Exame> findByIdAndCliente_Id(UUID id, UUID clienteId);
}
