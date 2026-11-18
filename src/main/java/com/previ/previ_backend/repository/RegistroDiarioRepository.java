
package com.previ.previ_backend.repository;

import com.previ.previ_backend.model.RegistroDiario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RegistroDiarioRepository
        extends JpaRepository<RegistroDiario, UUID> {

    // Verifica se a cliente já possui registro nessa data
    boolean existsByCliente_IdAndData(
            UUID clienteId,
            LocalDate data
    );

    // Lista os registros da cliente, dos mais recentes aos antigos
    List<RegistroDiario> findByCliente_IdOrderByDataDesc(
            UUID clienteId
    );

    // Busca um registro específico pertencente à cliente
    Optional<RegistroDiario> findByIdAndCliente_Id(
            UUID registroId,
            UUID clienteId
    );

    // Busca o registro da cliente em determinada data
    Optional<RegistroDiario> findByCliente_IdAndData(
            UUID clienteId,
            LocalDate data
    );
}
