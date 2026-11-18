
package com.previ.previ_backend.service;

import com.previ.previ_backend.dto.*;
import com.previ.previ_backend.model.Conteudo;
import com.previ.previ_backend.repository.ConteudoRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class ConteudoService {

    private final ConteudoRepository conteudoRepository;

    public ConteudoService(
            ConteudoRepository conteudoRepository) {
        this.conteudoRepository = conteudoRepository;
    }

    private Conteudo buscarPorId(UUID id) {
        return conteudoRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Conteúdo não encontrado"
                        )
                );
    }

    // ADMIN:
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ConteudoResponseDTO cadastrar(
            CriarConteudoDTO dto) {

        Conteudo conteudo = new Conteudo();

        conteudo.setTitulo(
                validarTexto(dto.titulo(), "Título", 200)
        );

        conteudo.setResumo(
                normalizarResumo(dto.resumo())
        );

        conteudo.setTexto(
                validarTexto(dto.texto(), "Texto", 0)
        );

        conteudo.setCategoria(
                validarTexto(dto.categoria(), "Categoria", 100)
        );

        conteudo.setPublicado(false);

        Conteudo salvo = conteudoRepository.saveAndFlush(conteudo);

        return new ConteudoResponseDTO(salvo);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ConteudoResponseDTO atualizar(
            UUID id, AtualizarConteudoDTO dto) {

        Conteudo conteudo = buscarPorId(id);

        if (dto.titulo() != null) {
            conteudo.setTitulo(
                    validarTexto(dto.titulo(), "Título", 200)
            );
        }

        if (dto.resumo() != null) {
            conteudo.setResumo(
                    normalizarResumo(dto.resumo())
            );
        }

        if (dto.texto() != null) {
            conteudo.setTexto(
                    validarTexto(dto.texto(), "Texto", 0)
            );
        }

        if (dto.categoria() != null) {
            conteudo.setCategoria(
                    validarTexto(dto.categoria(), "Categoria", 100)
            );
        }

        conteudoRepository.flush();

        return new ConteudoResponseDTO(conteudo);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ConteudoResponseDTO alterarPublicacao(
            UUID id, AlterarPublicacaoDTO dto) {

        if (dto.publicado() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Status de publicação obrigatório"
            );
        }

        Conteudo conteudo = buscarPorId(id);

        conteudo.setPublicado(dto.publicado());

        conteudoRepository.flush();

        return new ConteudoResponseDTO(conteudo);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void excluir(UUID id) {

        Conteudo conteudo = buscarPorId(id);

        conteudoRepository.delete(conteudo);
    }

    @Transactional(readOnly = true)
    public List<ConteudoResponseDTO> listarPublicados() {

        return conteudoRepository
                .findByPublicadoTrueOrderByCriadoEmDesc()
                .stream()
                .map(ConteudoResponseDTO::new)
                .toList();
    }

    // CLIENTE e ADMIN:
    @Transactional(readOnly = true)
    public ConteudoResponseDTO buscarPublicado(UUID id) {

        Conteudo conteudo = conteudoRepository
                .findByIdAndPublicadoTrue(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Conteúdo não encontrado"
                        )
                );

        return new ConteudoResponseDTO(conteudo);
    }

    // ADMIN:
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public List<ConteudoResponseDTO> listarTodos() {

        return conteudoRepository
                .findAllByOrderByCriadoEmDesc()
                .stream()
                .map(ConteudoResponseDTO::new)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public ConteudoResponseDTO buscarAdministrativo(UUID id) {
        return new ConteudoResponseDTO(buscarPorId(id));
    }

    // Validação
    private String validarTexto(
            String valor, String campo, int tamanhoMaximo) {

        if (valor == null || valor.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    campo + " não pode estar vazio"
            );
        }

        String normalizado = valor.trim();

        if (tamanhoMaximo > 0 &&
                normalizado.length() > tamanhoMaximo) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    campo + " excede o tamanho permitido"
            );
        }

        return normalizado;
    }

    // Resumo
    private String normalizarResumo(String resumo) {

        if (resumo == null || resumo.isBlank()) {
            return null;
        }

        String normalizado = resumo.trim();

        if (normalizado.length() > 500) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Resumo excede 500 caracteres"
            );
        }

        return normalizado;
    }
}
