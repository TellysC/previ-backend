    package com.previ.previ_backend.service;
    import com.previ.previ_backend.dto.ExameRequestDTO;
    import com.previ.previ_backend.dto.ExameResponseDTO;
    import com.previ.previ_backend.dto.ExameUpdateDTO;
    import com.previ.previ_backend.model.Cliente;
    import com.previ.previ_backend.model.Exame;
    import com.previ.previ_backend.repository.ExameRepository;
    import com.previ.previ_backend.enums.StatusExame;
    import org.springframework.http.HttpStatus;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;
    import org.springframework.web.server.ResponseStatusException;
    import java.util.List;
    import java.util.UUID;
    import java.time.LocalDate;
    import java.time.ZoneId;

    @Service
    public class ExameService {

        private static final ZoneId ZONA =
                ZoneId.of("America/Sao_Paulo");
        private final ExameRepository exameRepository;
        private final AuthClienteService authClienteService;

        public ExameService(
                ExameRepository exameRepository,
                AuthClienteService authClienteService
        ) {
            this.exameRepository = exameRepository;
            this.authClienteService = authClienteService;
        }

        // CADASTRAR EXAME
        @Transactional
        public ExameResponseDTO cadastrar(ExameRequestDTO dto) {

            Cliente cliente = authClienteService.obterClienteAutenticado();

            Exame exame = new Exame();

            exame.setCliente(cliente);
            exame.setNome(dto.nome());
            exame.setData(dto.data());
            exame.setHora(dto.hora());
            exame.setLocal(dto.local());
            exame.setObservacao(dto.observacao());

            Exame exameSalvo = exameRepository.save(exame);

            return ExameResponseDTO.fromEntity(exameSalvo);
        }

        // LISTAR EXAMES DO CLIENTE
        @Transactional(readOnly = true)
        public List<ExameResponseDTO> listar() {

            Cliente cliente = authClienteService.obterClienteAutenticado();

            return exameRepository.findByClienteId(cliente.getId())
                    .stream()
                    .map(ExameResponseDTO::fromEntity)
                    .toList();
        }

        // BUSCAR EXAME POR ID
        @Transactional(readOnly = true)
        public ExameResponseDTO buscarPorId(UUID exameId) {

            Exame exame = buscarExameDoCliente(exameId);

            return ExameResponseDTO.fromEntity(exame);
        }

        // ATUALIZAR EXAME
        @Transactional
        public ExameResponseDTO atualizar(
                UUID exameId,
                ExameUpdateDTO dto
        ) {

            Exame exame = buscarExameDoCliente(exameId);

            validarAlteracaoStatus(exame.getStatus(), dto.status());
            validarDatasExame(dto.status(), dto.dataRealizacao());
            exame.setNome(dto.nome());
            exame.setData(dto.data());
            exame.setHora(dto.hora());
            exame.setDataRealizacao(dto.dataRealizacao());
            exame.setLocal(dto.local());
            exame.setStatus(dto.status());
            exame.setObservacao(dto.observacao());

            return ExameResponseDTO.fromEntity(exame);
        }

        // Excluir exames
        @Transactional
        public void excluir(UUID exameId) {

            Exame exame = buscarExameDoCliente(exameId);

            exameRepository.delete(exame);
        }

        // Auxiliar para buscar exame do cliente autenticado
        private Exame buscarExameDoCliente(UUID exameId) {

            Cliente cliente = authClienteService.obterClienteAutenticado();

            return exameRepository
                    .findByIdAndCliente_Id(exameId, cliente.getId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Exame não encontrado"
                    ));
        }

        // Validações

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

        private void validarDatasExame(
                StatusExame status,
                LocalDate dataRealizacao
        ) {

            if (status == StatusExame.CONCLUIDO && dataRealizacao == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Exames concluídos devem possuir data de realização"
                );
            }

            if (status != StatusExame.CONCLUIDO && dataRealizacao != null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "A data de realização só pode ser informada em exames concluídos"
                );
            }

            if (dataRealizacao != null &&
                    dataRealizacao.isAfter(LocalDate.now(ZONA))) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "A data de realização não pode estar no futuro"
                );
            }
        }
    }
