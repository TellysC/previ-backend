package com.previ.previ_backend.dto;

import com.previ.previ_backend.model.Usuario;

import java.util.UUID;

public record UsuarioResponseDTO(

        UUID id,
        String nome,
        String email
) {
    public UsuarioResponseDTO (Usuario usuario) {
        this(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail()
        );
    }
}
