
package com.previ.previ_backend.service;

import com.previ.previ_backend.dto.PrevisaoCicloResponseDTO;
import com.previ.previ_backend.repository.CicloMenstrualRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class PrevisaoCicloService {

    private static final ZoneId ZONA =
            ZoneId.of("America/Sao_Paulo");

    private final CicloMenstrualRepository cicloRepository;
    private final AuthClienteService authClienteService;

    public PrevisaoCicloService(
            CicloMenstrualRepository cicloRepository,
            AuthClienteService authClienteService
    ) {
        this.cicloRepository = cicloRepository;
        this.authClienteService = authClienteService;
    }

    // Calcula a duração média dos ciclos registrados.
    private Double calcularMediaCiclo(List<LocalDate> datasInicio) {

        if (datasInicio.size() < 2) {
            return null;
        }

        long somaDias = 0;
        int quantidadeIntervalos = 0;

        for (int i = 0; i < datasInicio.size() - 1; i++) {

            long dias = ChronoUnit.DAYS.between(
                    datasInicio.get(i),
                    datasInicio.get(i + 1)
            );

            if (dias >= 21 && dias <= 45) {
                somaDias += dias;
                quantidadeIntervalos++;
            }
        }

        if (quantidadeIntervalos == 0) {
            return null;
        }

        double media = (double) somaDias / quantidadeIntervalos;

        return Math.round(media * 10.0) / 10.0;
    }

    // Calcula a diferença entre o maior e o menor intervalo.
    private Integer calcularVariacaoCiclo(List<LocalDate> datasInicio) {

        if (datasInicio.size() < 2) {
            return null;
        }

        Integer menorIntervalo = null;
        Integer maiorIntervalo = null;

        for (int i = 0; i < datasInicio.size() - 1; i++) {

            long dias = ChronoUnit.DAYS.between(
                    datasInicio.get(i),
                    datasInicio.get(i + 1)
            );

            if (dias >= 21 && dias <= 45) {

                int intervalo = (int) dias;

                if (menorIntervalo == null
                        || intervalo < menorIntervalo) {
                    menorIntervalo = intervalo;
                }

                if (maiorIntervalo == null
                        || intervalo > maiorIntervalo) {
                    maiorIntervalo = intervalo;
                }
            }
        }

        if (menorIntervalo == null) {
            return null;
        }

        return maiorIntervalo - menorIntervalo;
    }

    // Busca o histórico menstrual da cliente autenticada.
    private List<LocalDate> buscarDatasMenstruacoes() {

        var cliente = authClienteService.obterClienteAutenticado();

        return cicloRepository
                .findByClienteIdOrderByDataInicioDesc(cliente.getId())
                .stream()
                .map(ciclo -> ciclo.getDataInicio())
                .sorted()
                .toList();
    }

    // Estima a primeira menstruação após o último registro real.
    private LocalDate calcularProximaMenstruacao(
            List<LocalDate> datas,
            Double media
    ) {

        if (datas.isEmpty() || media == null) {
            return null;
        }

        LocalDate ultimaMenstruacao =
                datas.get(datas.size() - 1);

        int duracaoMedia = (int) Math.round(media);

        return ultimaMenstruacao.plusDays(duracaoMedia);
    }

    // Retorna os dados de previsão para o aplicativo.
    @Transactional(readOnly = true)
    public PrevisaoCicloResponseDTO obterPrevisao() {

        List<LocalDate> datas = buscarDatasMenstruacoes();

        LocalDate ultimaMenstruacao = datas.isEmpty()
                ? null
                : datas.get(datas.size() - 1);

        Double media = calcularMediaCiclo(datas);

        LocalDate proximaMenstruacao =
                calcularProximaMenstruacao(datas, media);

        LocalDate hoje = LocalDate.now(ZONA);

        boolean previsaoUltrapassada =
                proximaMenstruacao != null
                        && proximaMenstruacao.isBefore(hoje);

        int quantidadeCiclos = 0;

        for (int i = 0; i < datas.size() - 1; i++) {

            long dias = ChronoUnit.DAYS.between(
                    datas.get(i),
                    datas.get(i + 1)
            );

            if (dias >= 21 && dias <= 45) {
                quantidadeCiclos++;
            }
        }

        Integer variacaoCiclo = calcularVariacaoCiclo(datas);

        // Indicador conservador de variabilidade.
        // Não representa diagnóstico médico.
        boolean historicoMuitoVariavel =
                variacaoCiclo != null && variacaoCiclo > 7;

        LocalDate ovulacaoEstimada = null;
        LocalDate inicioJanelaFertilEstimada = null;
        LocalDate fimJanelaFertilEstimada = null;

        // Só apresenta datas férteis quando há histórico
        // suficiente, previsão atual e baixa variabilidade.
        if (proximaMenstruacao != null
                && !previsaoUltrapassada
                && quantidadeCiclos >= 3
                && !historicoMuitoVariavel) {

            ovulacaoEstimada =
                    proximaMenstruacao.minusDays(14);

            inicioJanelaFertilEstimada =
                    ovulacaoEstimada.minusDays(5);

            fimJanelaFertilEstimada =
                    ovulacaoEstimada.plusDays(1);
        }

        String aviso;

        if (media == null) {

            aviso = "Histórico insuficiente para calcular "
                    + "uma previsão individual.";

        } else if (previsaoUltrapassada) {

            aviso = "A data prevista para a próxima menstruação "
                    + "já passou. Atualize seu histórico menstrual "
                    + "para obter uma nova estimativa. "
                    + "Se houver possibilidade de gravidez, "
                    + "considere realizar um teste. "
                    + "As previsões não devem ser utilizadas "
                    + "como método contraceptivo.";

        } else if (historicoMuitoVariavel) {

            aviso = "Os intervalos menstruais registrados "
                    + "apresentam variação considerável. "
                    + "A previsão menstrual é aproximada "
                    + "e não é possível estimar a ovulação "
                    + "com confiança apenas pelo calendário. "
                    + "Não utilize essas previsões "
                    + "como método contraceptivo.";

        } else if (quantidadeCiclos < 3) {

            aviso = "O histórico ainda é limitado. "
                    + "Registre mais menstruações para melhorar "
                    + "as estimativas. A ovulação não pode ser "
                    + "confirmada apenas pelo calendário. "
                    + "Não utilize essas previsões "
                    + "como método contraceptivo.";

        } else {

            aviso = "As datas de menstruação, ovulação "
                    + "e janela fértil são estimativas "
                    + "baseadas no histórico registrado. "
                    + "A ovulação não pode ser confirmada "
                    + "apenas pelo calendário, e as previsões "
                    + "não devem ser utilizadas "
                    + "como método contraceptivo.";
        }

        return new PrevisaoCicloResponseDTO(
                media,
                ultimaMenstruacao,
                proximaMenstruacao,
                quantidadeCiclos,
                variacaoCiclo,
                previsaoUltrapassada,
                ovulacaoEstimada,
                inicioJanelaFertilEstimada,
                fimJanelaFertilEstimada,
                aviso
        );

    }
}
