
package com.previ.previ_backend.service;

import com.previ.previ_backend.dto.ConsultaRequestDTO;
import com.previ.previ_backend.dto.ConsultaResponseDTO;
import com.previ.previ_backend.dto.ConsultaUpdateDTO;
import com.previ.previ_backend.enums.StatusConsulta;
import com.previ.previ_backend.model.Cliente;
import com.previ.previ_backend.model.Consulta;
import com.previ.previ_backend.repository.ConsultaRepository;
import com.previ.previ_backend.dto.ConsultaRetornoRequestDTO;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
public class ConsultaService {

    private static final ZoneId ZONA = ZoneId.of("America/Sao_Paulo");

    private final ConsultaRepository consultaRepository;
    private final AuthClienteService authClienteService;

    public ConsultaService(
            ConsultaRepository consultaRepository,
            AuthClienteService authClienteService
    ) {
        this.consultaRepository = consultaRepository;
        this.authClienteService = authClienteService;
    }

    @Transactional
    public ConsultaResponseDTO cadastrar(ConsultaRequestDTO dto) {
        Cliente cliente = authClienteService.obterClienteAutenticado();

        Consulta consulta = new Consulta();
        consulta.setCliente(cliente);
        consulta.setEspecialidade(dto.especialidade());
        consulta.setProfissional(dto.profissional());
        consulta.setData(dto.data());
        consulta.setHora(dto.hora());
        consulta.setLocal(dto.local());
        consulta.setObservacao(dto.observacao());

        Consulta consultaSalva = consultaRepository.save(consulta);

        return ConsultaResponseDTO.fromEntity(consultaSalva);
    }

    @Transactional(readOnly = true)
    public List<ConsultaResponseDTO> listar() {
        Cliente cliente = authClienteService.obterClienteAutenticado();

        return consultaRepository.findByClienteId(cliente.getId())
                .stream()
                .map(ConsultaResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public ConsultaResponseDTO buscarPorId(UUID consultaId) {
        Consulta consulta = buscarConsultaDoCliente(consultaId);
        return ConsultaResponseDTO.fromEntity(consulta);
    }

    @Transactional
    public ConsultaResponseDTO atualizar(
            UUID consultaId,
            ConsultaUpdateDTO dto
    ) {
        Consulta consulta = buscarConsultaDoCliente(consultaId);

        validarAlteracaoStatus(consulta.getStatus(), dto.status());
        validarDataRealizacao(dto.status(), dto.dataRealizacao());

        consulta.setEspecialidade(dto.especialidade());
        consulta.setProfissional(dto.profissional());
        consulta.setData(dto.data());
        consulta.setHora(dto.hora());
        consulta.setDataRealizacao(dto.dataRealizacao());
        consulta.setLocal(dto.local());
        consulta.setStatus(dto.status());
        consulta.setObservacao(dto.observacao());

        return ConsultaResponseDTO.fromEntity(consulta);
    }

    @Transactional
    public void excluir(UUID consultaId) {
        Consulta consulta = buscarConsultaDoCliente(consultaId);
        consultaRepository.delete(consulta);
    }

    private Consulta buscarConsultaDoCliente(UUID consultaId) {
        Cliente cliente = authClienteService.obterClienteAutenticado();

        return consultaRepository
                .findByIdAndCliente_Id(consultaId, cliente.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Consulta não encontrada"
                ));
    }

    private void validarAlteracaoStatus(
            StatusConsulta statusAtual,
            StatusConsulta novoStatus
    ) {
        if (statusAtual == novoStatus) {
            return;
        }

        if (statusAtual == StatusConsulta.CONCLUIDA
                || statusAtual == StatusConsulta.CANCELADA) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Não é permitido alterar o status de uma consulta concluída ou cancelada"
            );
        }
    }

    private void validarDataRealizacao(
            StatusConsulta status,
            LocalDate dataRealizacao
    ) {
        if (status == StatusConsulta.CONCLUIDA
                && dataRealizacao == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Consultas concluídas devem possuir data de realização"
            );
        }

        if (dataRealizacao != null
                && dataRealizacao.isAfter(LocalDate.now(ZONA))) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A data de realização não pode estar no futuro"
            );
        }

        if (status != StatusConsulta.CONCLUIDA
                && dataRealizacao != null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A data de realização só pode ser informada em consultas concluídas"
            );
        }
    }

    @Transactional
    public ConsultaResponseDTO agendarRetorno(
            UUID consultaId,
            ConsultaRetornoRequestDTO dto
    ) {
        // Localiza a consulta e verifica se pertence à cliente autenticada
        Consulta consultaOriginal = buscarConsultaDoCliente(consultaId);

        // Somente consultas concluídas podem gerar retornos
        if (consultaOriginal.getStatus() != StatusConsulta.CONCLUIDA) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Somente consultas concluídas podem gerar retornos"
            );
        }

        // Cria uma nova consulta vinculada à original
        Consulta retorno = new Consulta();

        retorno.setCliente(consultaOriginal.getCliente());
        retorno.setConsultaOrigem(consultaOriginal);
        retorno.setEspecialidade(consultaOriginal.getEspecialidade());
        retorno.setProfissional(dto.profissional() != null
                ? dto.profissional()
                : consultaOriginal.getProfissional());
        retorno.setData(dto.data());
        retorno.setHora(dto.hora());
        retorno.setLocal(dto.local() != null
                ? dto.local()
                : consultaOriginal.getLocal());
        retorno.setObservacao(dto.observacao());
        retorno.setStatus(StatusConsulta.AGENDADA);

        Consulta retornoSalvo = consultaRepository.save(retorno);

        return ConsultaResponseDTO.fromEntity(retornoSalvo);
    }

    @Transactional
    public ConsultaResponseDTO concluir(UUID consultaId) {

        Consulta consulta = buscarConsultaDoCliente(consultaId);

        if (consulta.getStatus() == StatusConsulta.CONCLUIDA) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Esta consulta já foi concluída"
            );
        }

        if (consulta.getStatus() == StatusConsulta.CANCELADA) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Não é possível concluir uma consulta cancelada"
            );
        }

        LocalDate hoje = LocalDate.now(ZONA);

        if (consulta.getData().isAfter(hoje)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Não é possível concluir uma consulta futura"
            );
        }

        consulta.setStatus(StatusConsulta.CONCLUIDA);
        consulta.setDataRealizacao(hoje);

        return ConsultaResponseDTO.fromEntity(consulta);
    }
}
