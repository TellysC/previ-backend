
package com.previ.previ_backend.service;

import com.previ.previ_backend.dto.CicloMenstrualRequestDTO;
import com.previ.previ_backend.dto.CicloMenstrualResponseDTO;
import com.previ.previ_backend.model.CicloMenstrual;
import com.previ.previ_backend.model.Cliente;
import com.previ.previ_backend.repository.CicloMenstrualRepository;
import com.previ.previ_backend.dto.CicloMenstrualUpdateDTO;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
public class CicloMenstrualService {

    private static final ZoneId ZONA =
            ZoneId.of("America/Sao_Paulo");

    private final CicloMenstrualRepository cicloRepository;
    private final AuthClienteService authClienteService;

    public CicloMenstrualService(
            CicloMenstrualRepository cicloRepository,
            AuthClienteService authClienteService
    ) {
        this.cicloRepository = cicloRepository;
        this.authClienteService = authClienteService;
    }

    @Transactional
    public CicloMenstrualResponseDTO cadastrar(
            CicloMenstrualRequestDTO dto
    ) {
        Cliente cliente = authClienteService.obterClienteAutenticado();

        validarDatas(dto.dataInicio(), dto.dataFim());

        LocalDate dataFimVerificacao = dto.dataFim() != null
                ? dto.dataFim()
                : LocalDate.now(ZONA);

        boolean existeSobreposicao = cicloRepository.existeSobreposicao(
                cliente.getId(),
                null,
                dto.dataInicio(),
                dataFimVerificacao
        );

        if (existeSobreposicao) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Já existe um registro menstrual nesse período."
            );
        }

        CicloMenstrual ciclo = new CicloMenstrual();
        ciclo.setCliente(cliente);
        ciclo.setDataInicio(dto.dataInicio());
        ciclo.setDataFim(dto.dataFim());
        ciclo.setObservacao(dto.observacao());

        CicloMenstrual salvo = cicloRepository.save(ciclo);

        return converterParaDTO(salvo);
    }

    @Transactional
    public CicloMenstrualResponseDTO atualizar(
            UUID cicloId,
            CicloMenstrualUpdateDTO dto
    ) {
        CicloMenstrual ciclo = buscarCicloDoCliente(cicloId);

        LocalDate novaDataInicio = dto.dataInicio() != null
                ? dto.dataInicio()
                : ciclo.getDataInicio();

        LocalDate novaDataFim = dto.dataFim() != null
                ? dto.dataFim()
                : ciclo.getDataFim();

        validarDatas(novaDataInicio, novaDataFim);

        LocalDate dataFimVerificacao = novaDataFim != null
                ? novaDataFim
                : LocalDate.now(ZONA);

        boolean existeSobreposicao = cicloRepository.existeSobreposicao(
                ciclo.getCliente().getId(),
                cicloId,
                novaDataInicio,
                dataFimVerificacao
        );

        if (existeSobreposicao) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Já existe outro registro menstrual nesse período."
            );
        }

        ciclo.setDataInicio(novaDataInicio);
        ciclo.setDataFim(novaDataFim);

        if (dto.observacao() != null) {
            ciclo.setObservacao(dto.observacao());
        }

        return converterParaDTO(ciclo);
    }

    @Transactional
    public void excluir(UUID cicloId) {
        CicloMenstrual ciclo = buscarCicloDoCliente(cicloId);
        cicloRepository.delete(ciclo);
    }

    @Transactional(readOnly = true)
    public List<CicloMenstrualResponseDTO> listar() {
        Cliente cliente = authClienteService.obterClienteAutenticado();

        return cicloRepository
                .findByClienteIdOrderByDataInicioDesc(cliente.getId())
                .stream()
                .map(this::converterParaDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public CicloMenstrualResponseDTO buscarPorId(UUID cicloId) {
        return converterParaDTO(buscarCicloDoCliente(cicloId));
    }

    private CicloMenstrual buscarCicloDoCliente(UUID cicloId) {
        Cliente cliente = authClienteService.obterClienteAutenticado();

        return cicloRepository
                .findByIdAndCliente_Id(cicloId, cliente.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Registro menstrual não encontrado"
                ));
    }

    private void validarDatas(
            LocalDate dataInicio,
            LocalDate dataFim
    ) {
        if (dataInicio == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A data de início é obrigatória"
            );
        }

        if (dataInicio.isAfter(LocalDate.now(ZONA))) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A data de início não pode estar no futuro"
            );
        }

        if (dataFim != null && dataFim.isAfter(LocalDate.now(ZONA))) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A data final não pode estar no futuro"
            );
        }

        if (dataFim != null && dataFim.isBefore(dataInicio)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A data final não pode ser anterior à inicial"
            );
        }
    }

    private CicloMenstrualResponseDTO converterParaDTO(
            CicloMenstrual ciclo
    ) {
        return new CicloMenstrualResponseDTO(
                ciclo.getId(),
                ciclo.getDataInicio(),
                ciclo.getDataFim(),
                ciclo.getObservacao()
        );
    }
}
