package com.previ.previ_backend.service;

import com.previ.previ_backend.dto.*;
import com.previ.previ_backend.exceptions.EmailJaCadastradoException;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.previ.previ_backend.model.Cliente;
import com.previ.previ_backend.model.Usuario;
import com.previ.previ_backend.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private Usuario buscarAtivo(UUID id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario não encontrado"));
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarUsuarioAutenticado(UUID id) {
        Usuario usuario = buscarAtivo(id);

        return new UsuarioResponseDTO(usuario);
    }

    @Transactional
    public UsuarioResponseDTO atualizarPerfil(
            UUID id, AtualizarPerfilDTO dto) {

        Usuario usuario = buscarAtivo(id);

        if (dto.nome() != null) {
            String nome = dto.nome().trim();

            if (nome.isEmpty() || nome.length() > 255) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Nome inválido"
                );
            }

            usuario.setNome(nome);
        }

        if (usuario instanceof Cliente cliente) {

            if (dto.dataNasc() != null) {
                if (!dto.dataNasc().isBefore(LocalDate.now())) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Data de nascimento inválida"
                    );
                }

                cliente.setDataNasc(dto.dataNasc());
            }

            if (dto.telefones() != null) {
                Set<String> telefones = new HashSet<>();

                for (String telefone : dto.telefones()) {
                    if (telefone == null) {
                        throw new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "Telefone inválido"
                        );
                    }

                    String numero = telefone.replaceAll("\\D", "");

                    if (!numero.matches("\\d{10,11}")) {
                        throw new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "Telefone deve ter DDD e 10 ou 11 digitos"
                        );
                    }

                    telefones.add(numero);
                }

                cliente.setTelefone(telefones);
            }

        } else if (dto.dataNasc() != null || dto.telefones() != null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Campos exclusivos do cliente"
            );
        }

        return new UsuarioResponseDTO(usuario);
    }

    private void validarSenhaAtual(Usuario usuario, String senhaAtual) {

        if (senhaAtual == null ||
                !passwordEncoder.matches(senhaAtual, usuario.getSenha())) {

            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Senha incorreta");
        }
    }

    @Transactional
    public UsuarioResponseDTO alterarEmail(UUID id, AlterarEmailDTO dto) {
        Usuario usuario = buscarAtivo(id);

        validarSenhaAtual(usuario, dto.senhaAtual());

        String novoEmail = dto.novoEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (!usuario.getEmail().equalsIgnoreCase(novoEmail)
                && usuarioRepository
                .existsByEmailIgnoreCase(novoEmail)) {
            throw new EmailJaCadastradoException();
        }

        usuario.setEmail(novoEmail);
        return new UsuarioResponseDTO(usuario);
    }

    @Transactional
    public void alterarSenha(
            UUID id, AlterarSenhaDTO dto) {

        Usuario usuario = buscarAtivo(id);

        validarSenhaAtual(usuario, dto.senhaAtual());

        if (dto.novaSenha()
                .getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Senha excede 72 bytes"
            );
        }

        usuario.setSenha(
                passwordEncoder.encode(dto.novaSenha())
        );
    }

    @Transactional
    public void desativarConta(UUID id, ConfirmarSenhaDTO dto) {

        Usuario usuario = buscarAtivo(id);

        validarSenhaAtual(usuario, dto.senhaAtual());

        usuario.setDeletedAt(LocalDateTime.now());
    }
}
