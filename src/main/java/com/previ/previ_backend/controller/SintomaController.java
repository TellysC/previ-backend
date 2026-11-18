
package com.previ.previ_backend.controller;

import com.previ.previ_backend.dto.SintomaResponseDTO;
import com.previ.previ_backend.repository.SintomaRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sintomas")
public class SintomaController {

    private final SintomaRepository sintomaRepository;

    public SintomaController(SintomaRepository sintomaRepository) {
        this.sintomaRepository = sintomaRepository;
    }

    @GetMapping
    public ResponseEntity<List<SintomaResponseDTO>> listar() {

        List<SintomaResponseDTO> sintomas = sintomaRepository
                .findAll()
                .stream()
                .map(SintomaResponseDTO::fromEntity)
                .sorted((a, b) ->
                        a.nome().compareToIgnoreCase(b.nome()))
                .toList();

        return ResponseEntity.ok(sintomas);
    }
}
