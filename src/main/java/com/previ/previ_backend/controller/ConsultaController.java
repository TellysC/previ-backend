
package com.previ.previ_backend.controller;

import com.previ.previ_backend.dto.ConsultaRequestDTO;
import com.previ.previ_backend.dto.ConsultaResponseDTO;
import com.previ.previ_backend.dto.ConsultaUpdateDTO;
import com.previ.previ_backend.service.ConsultaService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/consultas")
public class ConsultaController {

    private final ConsultaService consultaService;

    public ConsultaController(ConsultaService consultaService) {
        this.consultaService = consultaService;
    }

    @PostMapping
    public ResponseEntity<ConsultaResponseDTO> cadastrar(
            @Valid @RequestBody ConsultaRequestDTO dto
    ) {
        ConsultaResponseDTO consulta = consultaService.cadastrar(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(consulta);
    }

    @GetMapping
    public ResponseEntity<List<ConsultaResponseDTO>> listar() {
        return ResponseEntity.ok(consultaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultaResponseDTO> buscarPorId(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(consultaService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConsultaResponseDTO> atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody ConsultaUpdateDTO dto
    ) {
        return ResponseEntity.ok(consultaService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        consultaService.excluir(id);

        return ResponseEntity.noContent().build();
    }
}
