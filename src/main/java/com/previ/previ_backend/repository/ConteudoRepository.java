package com.previ.previ_backend.repository;

import com.previ.previ_backend.model.Conteudo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConteudoRepository
        extends JpaRepository<Conteudo, UUID> {

    List<Conteudo> findByPublicadoTrueOrderByCriadoEmDesc();

    Optional<Conteudo> findByIdAndPublicadoTrue(UUID id);

    List<Conteudo> findAllByOrderByCriadoEmDesc();
}