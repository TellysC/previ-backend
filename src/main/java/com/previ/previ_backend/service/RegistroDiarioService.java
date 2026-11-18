
package com.previ.previ_backend.service;

import com.previ.previ_backend.dto.RegistroDiarioRequestDTO;
import com.previ.previ_backend.dto.RegistroDiarioResponseDTO;
import com.previ.previ_backend.model.Cliente;
import com.previ.previ_backend.model.RegistroDiario;
import com.previ.previ_backend.model.Sintoma;
import com.previ.previ_backend.repository.RegistroDiarioRepository;
import com.previ.previ_backend.repository.SintomaRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RegistroDiarioService {

    private final RegistroDiarioRepository registroRepository;
    private final SintomaRepository sintomaRepository;
    private final AuthClienteService authClienteService;

    public RegistroDiarioService(
            RegistroDiarioRepository registroRepository,
            SintomaRepository sintomaRepository,
            AuthClienteService authClienteService
    ) {
        this.registroRepository = registroRepository;
        this.sintomaRepository = sintomaRepository;
        this.authClienteService = authClienteService;
    }

    @Transactional
    public RegistroDiarioResponseDTO cadastrar(
            RegistroDiarioRequestDTO dto
    ) {
        Cliente cliente = authClienteService.obterClienteAutenticado();

        // Impede dois registros da mesma cliente na mesma data
        if (registroRepository.existsByCliente_IdAndData(
                cliente.getId(), dto.data())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Já existe um registro para esta data"
            );
        }

        RegistroDiario registro = new RegistroDiario();

        registro.setCliente(cliente);
        registro.setData(dto.data());
        registro.setObservacao(dto.observacao());
        registro.setFluxo(dto.fluxo());
        registro.setDor(dto.dor());
        registro.setHumor(dto.humor());
        registro.setSintomas(buscarSintomas(dto.sintomasIds()));

        RegistroDiario salvo = registroRepository.save(registro);

        return RegistroDiarioResponseDTO.fromEntity(salvo);
    }

    private Set<Sintoma> buscarSintomas(Set<UUID> sintomasIds) {

        if (sintomasIds == null || sintomasIds.isEmpty()) {
            return new HashSet<>();
        }

        Set<Sintoma> sintomas = new HashSet<>(
                sintomaRepository.findAllById(sintomasIds)
        );

        Set<UUID> idsEncontrados = sintomas.stream()
                .map(Sintoma::getId)
                .collect(Collectors.toSet());

        if (!idsEncontrados.containsAll(sintomasIds)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Um ou mais sintomas informados não existem"
            );
        }

        return sintomas;
    }

    @Transactional(readOnly = true)
    public List<RegistroDiarioResponseDTO> listar() {

        Cliente cliente = authClienteService.obterClienteAutenticado();

        return registroRepository
                .findByCliente_IdOrderByDataDesc(cliente.getId())
                .stream()
                .map(RegistroDiarioResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public RegistroDiarioResponseDTO buscarPorId(UUID registroId) {

        RegistroDiario registro = buscarRegistroDoCliente(registroId);

        return RegistroDiarioResponseDTO.fromEntity(registro);
    }

    private RegistroDiario buscarRegistroDoCliente(UUID registroId) {

        Cliente cliente = authClienteService.obterClienteAutenticado();

        return registroRepository
                .findByIdAndCliente_Id(registroId, cliente.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Registro diário não encontrado"
                ));
    }

    @Transactional(readOnly = true)
    public RegistroDiarioResponseDTO buscarPorData(LocalDate data) {

        Cliente cliente = authClienteService.obterClienteAutenticado();

        RegistroDiario registro = registroRepository
                .findByCliente_IdAndData(cliente.getId(), data)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Nenhum registro encontrado para esta data"
                ));

        return RegistroDiarioResponseDTO.fromEntity(registro);
    }

    @Transactional
    public RegistroDiarioResponseDTO atualizar(
            UUID registroId,
            RegistroDiarioRequestDTO dto
    ) {
        // Busca somente registros da cliente autenticada
        RegistroDiario registro = buscarRegistroDoCliente(registroId);

        // Impede mudar a data para outra que já possui registro
        if (!registro.getData().equals(dto.data())) {

            Cliente cliente = authClienteService.obterClienteAutenticado();

            if (registroRepository.existsByCliente_IdAndData(
                    cliente.getId(), dto.data())) {

                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Já existe um registro para esta data"
                );
            }
        }

        // Atualiza os dados
        registro.setData(dto.data());
        registro.setObservacao(dto.observacao());
        registro.setFluxo(dto.fluxo());
        registro.setDor(dto.dor());
        registro.setHumor(dto.humor());
        registro.setSintomas(buscarSintomas(dto.sintomasIds()));

        return RegistroDiarioResponseDTO.fromEntity(registro);
    }

    @Transactional
    public void excluir(UUID registroId) {

        // Busca o registro e verifica se pertence à cliente autenticada
        RegistroDiario registro = buscarRegistroDoCliente(registroId);

        // Exclui o registro do banco de dados
        registroRepository.delete(registro);
    }
}
