
package com.previ.previ_backend.service;

import com.previ.previ_backend.dto.VacinaRequestDTO;
import com.previ.previ_backend.dto.VacinaResponseDTO;
import com.previ.previ_backend.dto.VacinaUpdateDTO;
import com.previ.previ_backend.model.Cliente;
import com.previ.previ_backend.model.Vacina;
import com.previ.previ_backend.repository.VacinaRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
public class VacinaService {

    private static final ZoneId ZONA =
            ZoneId.of("America/Sao_Paulo");

    private final VacinaRepository vacinaRepository;
    private final AuthClienteService authClienteService;

    public VacinaService(
            VacinaRepository vacinaRepository,
            AuthClienteService authClienteService
    ) {
        this.vacinaRepository = vacinaRepository;
        this.authClienteService = authClienteService;
    }

    @Transactional
    public VacinaResponseDTO cadastrar(VacinaRequestDTO dto) {
        Cliente cliente = authClienteService.obterClienteAutenticado();

        validarDatas(dto.dataAplicacao(), dto.proximaDose());

        Vacina vacina = new Vacina();
        vacina.setCliente(cliente);
        vacina.setNome(dto.nome());
        vacina.setDose(dto.dose());
        vacina.setDataAplicacao(dto.dataAplicacao());
        vacina.setProximaDose(dto.proximaDose());
        vacina.setLocalAplicacao(dto.localAplicacao());
        vacina.setObservacoes(dto.observacoes());

        Vacina vacinaSalva = vacinaRepository.save(vacina);

        return converterParaDTO(vacinaSalva);
    }

    @Transactional(readOnly = true)
    public List<VacinaResponseDTO> listar() {
        Cliente cliente = authClienteService.obterClienteAutenticado();

        return vacinaRepository.findByClienteId(cliente.getId())
                .stream()
                .map(this::converterParaDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public VacinaResponseDTO buscarPorId(UUID vacinaId) {
        return converterParaDTO(buscarVacinaDoCliente(vacinaId));
    }

    @Transactional
    public VacinaResponseDTO atualizar(
            UUID vacinaId,
            VacinaUpdateDTO dto
    ) {
        Vacina vacina = buscarVacinaDoCliente(vacinaId);

        validarDatas(dto.dataAplicacao(), dto.proximaDose());

        vacina.setNome(dto.nome());
        vacina.setDose(dto.dose());
        vacina.setDataAplicacao(dto.dataAplicacao());
        vacina.setProximaDose(dto.proximaDose());
        vacina.setLocalAplicacao(dto.localAplicacao());
        vacina.setObservacoes(dto.observacoes());

        return converterParaDTO(vacina);
    }

    @Transactional
    public void excluir(UUID vacinaId) {
        Vacina vacina = buscarVacinaDoCliente(vacinaId);
        vacinaRepository.delete(vacina);
    }

    private Vacina buscarVacinaDoCliente(UUID vacinaId) {
        Cliente cliente = authClienteService.obterClienteAutenticado();

        return vacinaRepository
                .findByIdAndCliente_Id(vacinaId, cliente.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Vacina não encontrada"
                ));
    }

    private void validarDatas(
            LocalDate dataAplicacao,
            LocalDate proximaDose
    ) {
        if (dataAplicacao.isAfter(LocalDate.now(ZONA))) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A data de aplicação não pode estar no futuro"
            );
        }

        if (proximaDose != null
                && !proximaDose.isAfter(dataAplicacao)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A próxima dose deve ser posterior à aplicação"
            );
        }
    }

    private VacinaResponseDTO converterParaDTO(Vacina vacina) {
        return new VacinaResponseDTO(
                vacina.getId(),
                vacina.getNome(),
                vacina.getDose(),
                vacina.getDataAplicacao(),
                vacina.getProximaDose(),
                vacina.getLocalAplicacao(),
                vacina.getObservacoes()
        );
    }
}
