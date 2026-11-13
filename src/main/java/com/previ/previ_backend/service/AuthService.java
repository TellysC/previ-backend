package com.previ.previ_backend.service;

import com.previ.previ_backend.dto.ClienteCadastroDTO;
import com.previ.previ_backend.dto.LoginRequestDTO;
import com.previ.previ_backend.dto.LoginResponseDTO;
import com.previ.previ_backend.dto.UsuarioResponseDTO;
import com.previ.previ_backend.model.Cliente;
import com.previ.previ_backend.model.Usuario;
import com.previ.previ_backend.repository.UsuarioRepository;
import com.previ.previ_backend.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(PasswordEncoder passwordEncoder, UsuarioRepository usuarioRepository, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.passwordEncoder = passwordEncoder;
        this.usuarioRepository = usuarioRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public UsuarioResponseDTO registrarCliente(ClienteCadastroDTO dto) {
        String email = dto.email().trim().toLowerCase(Locale.ROOT);
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            //EmailJaCadastradoException
            throw new IllegalArgumentException();
        }

        // BCrypt 72
        if (dto.senha().getBytes(StandardCharsets.UTF_8).length > 72) {
            //CadastroInvalidoException
            throw new IllegalArgumentException("Senha não pode exceder 72 bytes em UTF-8");
        }

        Set<String> telefones = new LinkedHashSet<>();
        for (String telefone : dto.telefones()) {
            if (telefone == null) {
                //CadastroInvalidoException
                throw new IllegalArgumentException("Telefone não pode ser nulo");
            }
            String normalizado = telefone.replaceAll("\\D", "");
            if (!normalizado.matches("\\d{10,11}")) {
                //CadastroInvalidoException
                throw new IllegalArgumentException("Telefone deve conter DDD e 10 ou 11 dígitos");
            }
            telefones.add(normalizado);
        }

        Cliente cliente = new Cliente();
        cliente.setNome(dto.nome().trim());
        cliente.setEmail(email);
        cliente.setSenha(passwordEncoder.encode(dto.senha()));
        cliente.setDataNasc(dto.dataNascimento());
        cliente.setTelefone(telefones);

        Cliente salvo = usuarioRepository.save(cliente);
        return new UsuarioResponseDTO(salvo);
    }

    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginRequestDTO dto) {
        String email = dto.email().trim().toLowerCase(Locale.ROOT);
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, dto.senha()));

        Usuario usuario = usuarioRepository.findByEmailIgnoreCaseAndDeletedAtIsNull(email)
                .orElseThrow(() -> new BadCredentialsException("Credenciais inválidas"));

        String accessToken = jwtService.gerarAccessToken(usuario, auth.getAuthorities());
        return new LoginResponseDTO(accessToken, "Bearer", jwtService.getExpiresInSeconds(),
                new UsuarioResponseDTO(usuario));
    }



}
