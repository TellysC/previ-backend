package com.previ.previ_backend.controller;

import com.previ.previ_backend.dto.ClienteCadastroDTO;
import com.previ.previ_backend.dto.LoginRequestDTO;
import com.previ.previ_backend.dto.LoginResponseDTO;
import com.previ.previ_backend.dto.UsuarioResponseDTO;
import com.previ.previ_backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UsuarioResponseDTO> registrar(@Valid @RequestBody ClienteCadastroDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrarCliente(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }
}

