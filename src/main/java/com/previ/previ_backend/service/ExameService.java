
package com.previ.previ_backend.service;

import com.previ.previ_backend.dto.ExameRequestDTO;
import com.previ.previ_backend.dto.ExameResponseDTO;
import com.previ.previ_backend.dto.ExameUpdateDTO;
import com.previ.previ_backend.model.Cliente;
import com.previ.previ_backend.model.Exame;
import com.previ.previ_backend.model.Usuario;
import com.previ.previ_backend.repository.ExameRepository;
import com.previ.previ_backend.repository.UsuarioRepository;
import com.previ.previ_backend.enums.StatusExame;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.UUID;
import java.time.LocalDate;

@Service
public class ExameService {

    private final ExameRepository exameRepository;
    private final UsuarioRepository usuarioRepository;

    public ExameService(
            ExameRepository exameRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.exameRepository = exameRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // CADASTRAR EXAME
    @Transactional
    public ExameResponseDTO cadastrar(ExameRequestDTO dto) {

        Cliente cliente = obterClienteAutenticado();

        if (dto.dataRealizacao() != null &&
                dto.dataRealizacao().isAfter(LocalDate.now())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A data de realização não pode estar no futuro"
            );
        }

        Exame exame = new Exame();

        exame.setCliente(cliente);
        exame.setNome(dto.nome());
        exame.setData(dto.data());
        exame.setHora(dto.hora());
        exame.setDataRealizacao(dto.dataRealizacao());
        exame.setLocal(dto.local());
        exame.setObservacao(dto.observacao());

        Exame exameSalvo = exameRepository.save(exame);

        return ExameResponseDTO.fromEntity(exameSalvo);
    }

    // LISTAR EXAMES DO CLIENTE
    @Transactional(readOnly = true)
    public List<ExameResponseDTO> listar() {

        Cliente cliente = obterClienteAutenticado();

        return exameRepository.findByClienteId(cliente.getId())
                .stream()
                .map(ExameResponseDTO::fromEntity)
                .toList();
    }

    // BUSCAR EXAME POR ID
    @Transactional(readOnly = true)
    public ExameResponseDTO buscarPorId(UUID exameId) {

        Cliente cliente = obterClienteAutenticado();

        Exame exame = exameRepository
                .findByIdAndCliente_Id(exameId, cliente.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Exame não encontrado"
                ));

        return ExameResponseDTO.fromEntity(exame);
    }

    // IDENTIFICAR O CLIENTE AUTENTICADO
    private Cliente obterClienteAutenticado() {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário não autenticado"
            );
        }

        UUID usuarioId;

        try {
            usuarioId = UUID.fromString(authentication.getName());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Identificador do usuário inválido"
            );
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Usuário não encontrado"
                ));

        if (!(usuario instanceof Cliente cliente)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Apenas clientes podem acessar exames"
            );
        }

        return cliente;
    }

    @Transactional
    public ExameResponseDTO atualizar(
            UUID exameId,
            ExameUpdateDTO dto
    ) {

        Cliente cliente = obterClienteAutenticado();

        Exame exame = exameRepository
                .findByIdAndCliente_Id(exameId, cliente.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Exame não encontrado"
                ));

        validarAlteracaoStatus(exame.getStatus(), dto.status());
        validarDatasExame(dto);

        exame.setNome(dto.nome());
        exame.setData(dto.data());
        exame.setHora(dto.hora());
        exame.setDataRealizacao(dto.dataRealizacao());
        exame.setLocal(dto.local());
        exame.setStatus(dto.status());
        exame.setObservacao(dto.observacao());

        Exame exameAtualizado = exameRepository.save(exame);

        return ExameResponseDTO.fromEntity(exameAtualizado);
    }

    @Transactional
    public void excluir(UUID exameId) {

        Cliente cliente = obterClienteAutenticado();

        Exame exame = exameRepository
                .findByIdAndCliente_Id(exameId, cliente.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Exame não encontrado"
                ));

        exameRepository.delete(exame);
    }


    private void validarAlteracaoStatus(
            StatusExame statusAtual,
            StatusExame novoStatus
    ) {

        if (statusAtual == novoStatus) {
            return;
        }

        if (statusAtual == StatusExame.CONCLUIDO ||
                statusAtual == StatusExame.CANCELADO) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Não é permitido alterar o status de um exame concluído ou cancelado"
            );
        }

    }

    private void validarDatasExame(ExameUpdateDTO dto) {

        if (dto.status() == StatusExame.CONCLUIDO) {

            if (dto.dataRealizacao() == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Exames concluídos devem possuir data de realização"
                );
            }
        }

        if (dto.dataRealizacao() != null &&
                dto.dataRealizacao().isAfter(LocalDate.now())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A data de realização não pode estar no futuro"
            );
        }
    }

}
