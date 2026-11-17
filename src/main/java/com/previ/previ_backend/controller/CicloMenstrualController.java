
    package com.previ.previ_backend.controller;

    import com.previ.previ_backend.dto.CicloMenstrualRequestDTO;
    import com.previ.previ_backend.dto.CicloMenstrualResponseDTO;
    import com.previ.previ_backend.dto.CicloMenstrualUpdateDTO;
    import com.previ.previ_backend.service.CicloMenstrualService;
    import com.previ.previ_backend.dto.PrevisaoCicloResponseDTO;
    import com.previ.previ_backend.service.PrevisaoCicloService;

    import jakarta.validation.Valid;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;
    import java.util.UUID;

    @RestController
    @RequestMapping("/api/ciclos")
    public class CicloMenstrualController {

        private final CicloMenstrualService cicloService;
        private final PrevisaoCicloService previsaoCicloService;

        public CicloMenstrualController(
                CicloMenstrualService cicloService,
                PrevisaoCicloService previsaoCicloService
        ) {
            this.cicloService = cicloService;
            this.previsaoCicloService = previsaoCicloService;
        }

        @PostMapping
        public ResponseEntity<CicloMenstrualResponseDTO> cadastrar(
                @Valid @RequestBody CicloMenstrualRequestDTO dto
        ) {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(cicloService.cadastrar(dto));
        }

        @GetMapping
        public ResponseEntity<List<CicloMenstrualResponseDTO>> listar() {
            return ResponseEntity.ok(cicloService.listar());
        }

        @GetMapping("/{id}")
        public ResponseEntity<CicloMenstrualResponseDTO> buscarPorId(
                @PathVariable UUID id
        ) {
            return ResponseEntity.ok(cicloService.buscarPorId(id));
        }

        @PutMapping("/{id}")
        public ResponseEntity<CicloMenstrualResponseDTO> atualizar(
                @PathVariable UUID id,
                @Valid @RequestBody CicloMenstrualUpdateDTO dto
        ) {
            return ResponseEntity.ok(cicloService.atualizar(id, dto));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> excluir(
                @PathVariable UUID id
        ) {
            cicloService.excluir(id);
            return ResponseEntity.noContent().build();
        }


        @GetMapping("/previsao")
        public ResponseEntity<PrevisaoCicloResponseDTO> obterPrevisao() {
            return ResponseEntity.ok(
                    previsaoCicloService.obterPrevisao()
            );
        }

    }
