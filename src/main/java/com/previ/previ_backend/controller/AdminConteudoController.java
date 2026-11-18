
package com.previ.previ_backend.controller;

import com.previ.previ_backend.dto.*;
import com.previ.previ_backend.service.ConteudoService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/conteudos")
@PreAuthorize("hasRole('ADMIN')")
public class AdminConteudoController {

    private final ConteudoService conteudoService;

    public AdminConteudoController(ConteudoService conteudoService) {
        this.conteudoService = conteudoService;
    }

    @PostMapping
    public ResponseEntity<ConteudoResponseDTO> cadastrar(
            @Valid @RequestBody CriarConteudoDTO dto) {

        ConteudoResponseDTO conteudo =
                conteudoService.cadastrar(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(conteudo);
    }

    @GetMapping
    public ResponseEntity<List<ConteudoResponseDTO>> listarTodos() {

        return ResponseEntity.ok(
                conteudoService.listarTodos()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConteudoResponseDTO> buscarPorId(
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                conteudoService.buscarAdministrativo(id)
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ConteudoResponseDTO> atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody AtualizarConteudoDTO dto) {

        return ResponseEntity.ok(
                conteudoService.atualizar(id, dto)
        );
    }

    @PatchMapping("/{id}/publicacao")
    public ResponseEntity<ConteudoResponseDTO> alterarPublicacao(
            @PathVariable UUID id,
            @Valid @RequestBody AlterarPublicacaoDTO dto) {

        return ResponseEntity.ok(
                conteudoService.alterarPublicacao(id, dto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable UUID id) {

        conteudoService.excluir(id);

        return ResponseEntity.noContent().build();
    }
}
