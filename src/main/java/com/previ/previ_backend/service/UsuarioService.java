package com.previ.previ_backend.service;

import com.previ.previ_backend.dto.UsuarioResponseDTO;
import com.previ.previ_backend.model.Usuario;
import com.previ.previ_backend.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public UsuarioResponseDTO buscarUsuarioAutenticado(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario não encontrado"));
        return new UsuarioResponseDTO(usuario);
    }
}
