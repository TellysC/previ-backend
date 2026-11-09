package com.previ.previ_backend.dto;

public record LoginResponseDTO(
        String acessToken,
        String tokenType,
        long expiresIn,
        UsuarioResponseDTO usuario
) { }
