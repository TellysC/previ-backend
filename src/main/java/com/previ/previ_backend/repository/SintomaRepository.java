
package com.previ.previ_backend.repository;

import com.previ.previ_backend.model.Sintoma;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SintomaRepository
        extends JpaRepository<Sintoma, UUID> {

    boolean existsByNomeIgnoreCase(String nome);
}
