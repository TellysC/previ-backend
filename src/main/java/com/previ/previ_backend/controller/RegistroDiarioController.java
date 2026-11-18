
package com.previ.previ_backend.controller;

import com.previ.previ_backend.dto.RegistroDiarioRequestDTO;
import com.previ.previ_backend.dto.RegistroDiarioResponseDTO;
import com.previ.previ_backend.service.RegistroDiarioService;

import jakarta.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/registros-diarios")
public class RegistroDiarioController {

    private final RegistroDiarioService registroDiarioService;

    public RegistroDiarioController(
            RegistroDiarioService registroDiarioService
    ) {
        this.registroDiarioService = registroDiarioService;
    }

    @PostMapping
    public ResponseEntity<RegistroDiarioResponseDTO> cadastrar(
            @Valid @RequestBody RegistroDiarioRequestDTO dto
    ) {
        RegistroDiarioResponseDTO registro =
                registroDiarioService.cadastrar(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(registro);
    }

    @GetMapping
    public ResponseEntity<List<RegistroDiarioResponseDTO>> listar() {
        return ResponseEntity.ok(registroDiarioService.listar());
    }

    @GetMapping("/data/{data}")
    public ResponseEntity<RegistroDiarioResponseDTO> buscarPorData(
            @PathVariable
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate data
    ) {
        return ResponseEntity.ok(
                registroDiarioService.buscarPorData(data)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistroDiarioResponseDTO> buscarPorId(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                registroDiarioService.buscarPorId(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<RegistroDiarioResponseDTO> atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody RegistroDiarioRequestDTO dto
    ) {
        return ResponseEntity.ok(
                registroDiarioService.atualizar(id, dto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        registroDiarioService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
