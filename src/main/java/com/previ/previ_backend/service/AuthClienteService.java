
package com.previ.previ_backend.service;

import com.previ.previ_backend.model.Cliente;
import com.previ.previ_backend.model.Usuario;
import com.previ.previ_backend.repository.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class AuthClienteService {

    private final UsuarioRepository usuarioRepository;

    public AuthClienteService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Cliente obterClienteAutenticado() {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário não autenticado"
            );
        }

        UUID usuarioId;

        try {
            usuarioId = UUID.fromString(authentication.getName());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Identificador do usuário inválido"
            );
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Usuário não encontrado"
                ));

        if (!(usuario instanceof Cliente cliente)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Apenas clientes podem acessar este recurso"
            );
        }

        return cliente;
    }
}
