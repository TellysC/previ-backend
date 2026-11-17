
package com.previ.previ_backend.controller;

import com.previ.previ_backend.dto.VacinaRequestDTO;
import com.previ.previ_backend.dto.VacinaResponseDTO;
import com.previ.previ_backend.dto.VacinaUpdateDTO;
import com.previ.previ_backend.service.VacinaService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/vacinas")
public class VacinaController {

    private final VacinaService vacinaService;

    public VacinaController(VacinaService vacinaService) {
        this.vacinaService = vacinaService;
    }

    @PostMapping
    public ResponseEntity<VacinaResponseDTO> cadastrar(
            @Valid @RequestBody VacinaRequestDTO dto
    ) {
        VacinaResponseDTO vacina = vacinaService.cadastrar(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(vacina);
    }

    @GetMapping
    public ResponseEntity<List<VacinaResponseDTO>> listar() {
        return ResponseEntity.ok(vacinaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VacinaResponseDTO> buscarPorId(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(vacinaService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VacinaResponseDTO> atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody VacinaUpdateDTO dto
    ) {
        return ResponseEntity.ok(vacinaService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable UUID id
    ) {
        vacinaService.excluir(id);

        return ResponseEntity.noContent().build();
    }
}
