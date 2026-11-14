package com.previ.previ_backend.controller;
import com.previ.previ_backend.dto.ExameRequestDTO;
import com.previ.previ_backend.dto.ExameResponseDTO;
import com.previ.previ_backend.dto.ExameUpdateDTO;
import com.previ.previ_backend.service.ExameService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/exames")
public class ExameController {

    private final ExameService exameService;

    public ExameController(ExameService exameService) {
        this.exameService = exameService;
    }

    @PostMapping
    public ResponseEntity<ExameResponseDTO> cadastrar(
            @Valid @RequestBody ExameRequestDTO dto
    ) {
        ExameResponseDTO exame = exameService.cadastrar(dto);

        URI location = URI.create("/api/exames/" + exame.id());
        return ResponseEntity.created(location).body(exame);
    }

    @GetMapping
    public ResponseEntity<List<ExameResponseDTO>> listar() {
        return ResponseEntity.ok(exameService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExameResponseDTO> buscarPorId(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(exameService.buscarPorId(id));
    }


    @PutMapping("/{id}")
    public ResponseEntity<ExameResponseDTO> atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody ExameUpdateDTO dto
    ) {
        return ResponseEntity.ok(exameService.atualizar(id, dto));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {

        exameService.excluir(id);

        return ResponseEntity.noContent().build();
    }
}
