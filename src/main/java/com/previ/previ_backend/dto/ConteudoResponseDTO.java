package com.previ.previ_backend.dto;

import com.previ.previ_backend.model.Conteudo;

import java.time.LocalDateTime;
import java.util.UUID;

public record ConteudoResponseDTO(
        UUID id,
        String titulo,
        String resumo,
        String texto,
        String categoria,
        boolean publicado,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm

) {
    public ConteudoResponseDTO(Conteudo conteudo) {
        this(
                conteudo.getId(),
                conteudo.getTitulo(),
                conteudo.getResumo(),
                conteudo.getTexto(),
                conteudo.getCategoria(),
                conteudo.isPublicado(),
                conteudo.getCriadoEm(),
                conteudo.getAtualizadoEm()
        );
    }
}
