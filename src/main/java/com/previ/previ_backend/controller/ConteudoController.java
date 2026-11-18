package com.previ.previ_backend.controller;

import com.previ.previ_backend.dto.ConteudoResponseDTO;
import com.previ.previ_backend.service.ConteudoService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/conteudos")
@PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
public class ConteudoController {

    private final ConteudoService conteudoService;

    public ConteudoController(ConteudoService conteudoService) {
        this.conteudoService = conteudoService;
    }

    @GetMapping
    public ResponseEntity<List<ConteudoResponseDTO>> listar() {

        return ResponseEntity.ok(
                conteudoService.listarPublicados()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConteudoResponseDTO> buscarPorId(
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                conteudoService.buscarPublicado(id)
        );
    }
}