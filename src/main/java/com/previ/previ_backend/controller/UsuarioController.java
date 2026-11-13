package com.previ.previ_backend.controller;

import com.previ.previ_backend.dto.*;
import com.previ.previ_backend.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> me(
            @AuthenticationPrincipal Jwt jwt) {

        UUID id = UUID.fromString(jwt.getSubject());

        return ResponseEntity.ok(usuarioService.buscarUsuarioAutenticado(id));
    }

    @PatchMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> atualizar(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody AtualizarPerfilDTO dto) {

        UUID id = UUID.fromString(jwt.getSubject());

        return ResponseEntity.ok(usuarioService.atualizarPerfil(id, dto));
    }

    @PatchMapping("/me/email")
    public ResponseEntity<UsuarioResponseDTO> alterarEmail(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody AlterarEmailDTO dto) {

        UUID id = UUID.fromString(jwt.getSubject());

        return ResponseEntity.ok(usuarioService.alterarEmail(id, dto));
    }

    @PatchMapping("/me/senha")
    public ResponseEntity<Void> alterarSenha(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody AlterarSenhaDTO dto){

        UUID id = UUID.fromString(jwt.getSubject());
        usuarioService.alterarSenha(id, dto);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> desativar(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ConfirmarSenhaDTO dto){

        UUID id = UUID.fromString(jwt.getSubject());
        usuarioService.desativarConta(id, dto);

        return ResponseEntity.noContent().build();
    }
}

