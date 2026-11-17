
package com.previ.previ_backend;

import com.previ.previ_backend.dto.PrevisaoCicloResponseDTO;
import com.previ.previ_backend.model.CicloMenstrual;
import com.previ.previ_backend.model.Cliente;
import com.previ.previ_backend.repository.CicloMenstrualRepository;
import com.previ.previ_backend.service.AuthClienteService;
import com.previ.previ_backend.service.PrevisaoCicloService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrevisaoCicloServiceTest {

    @Mock
    private CicloMenstrualRepository cicloRepository;

    @Mock
    private AuthClienteService authClienteService;

    @InjectMocks
    private PrevisaoCicloService previsaoService;

    private Cliente cliente;

    @BeforeEach
    void configurar() {
        cliente = new Cliente();
        cliente.setId(UUID.randomUUID());
    }

    private CicloMenstrual criarCiclo(LocalDate dataInicio) {
        CicloMenstrual ciclo = new CicloMenstrual();
        ciclo.setCliente(cliente);
        ciclo.setDataInicio(dataInicio);
        ciclo.setDataFim(dataInicio.plusDays(4));
        return ciclo;
    }

    @Test
    void deveCalcularMediaVariacaoEProximaMenstruacao() {

        List<CicloMenstrual> ciclos = List.of(
                criarCiclo(LocalDate.of(2026, 8, 1)),
                criarCiclo(LocalDate.of(2026, 8, 29)),
                criarCiclo(LocalDate.of(2026, 9, 28)),
                criarCiclo(LocalDate.of(2026, 10, 28))
        );

        when(authClienteService.obterClienteAutenticado())
                .thenReturn(cliente);

        when(cicloRepository.findByClienteIdOrderByDataInicioDesc(
                cliente.getId()
        )).thenReturn(ciclos);

        PrevisaoCicloResponseDTO resultado =
                previsaoService.obterPrevisao();

        assertEquals(29.3, resultado.mediaDuracaoCiclo());
        assertEquals(2, resultado.variacaoDuracaoCiclo());
        assertEquals(3, resultado.quantidadeCiclosAnalisados());

        assertEquals(
                LocalDate.of(2026, 10, 28),
                resultado.ultimaMenstruacao()
        );

        assertEquals(
                LocalDate.of(2026, 11, 26),
                resultado.proximaMenstruacaoEstimada()
        );
    }

    @Test
    void naoDevePreverSemHistoricoMenstrual() {

        when(authClienteService.obterClienteAutenticado())
                .thenReturn(cliente);

        when(cicloRepository.findByClienteIdOrderByDataInicioDesc(
                cliente.getId()
        )).thenReturn(List.of());

        PrevisaoCicloResponseDTO resultado =
                previsaoService.obterPrevisao();

        assertNull(resultado.mediaDuracaoCiclo());
        assertNull(resultado.ultimaMenstruacao());
        assertNull(resultado.proximaMenstruacaoEstimada());

        assertEquals(0, resultado.quantidadeCiclosAnalisados());

        assertNull(resultado.variacaoDuracaoCiclo());
        assertNull(resultado.ovulacaoEstimada());
        assertNull(resultado.inicioJanelaFertilEstimada());
        assertNull(resultado.fimJanelaFertilEstimada());

        assertFalse(resultado.previsaoUltrapassada());

        assertTrue(
                resultado.aviso().contains("Histórico insuficiente")
        );
    }

    @Test
    void naoDevePreverComApenasUmaMenstruacao() {

        LocalDate dataInicio = LocalDate.of(2026, 10, 28);

        when(authClienteService.obterClienteAutenticado())
                .thenReturn(cliente);

        when(cicloRepository.findByClienteIdOrderByDataInicioDesc(
                cliente.getId()
        )).thenReturn(List.of(criarCiclo(dataInicio)));

        PrevisaoCicloResponseDTO resultado =
                previsaoService.obterPrevisao();

        assertEquals(
                dataInicio,
                resultado.ultimaMenstruacao()
        );

        assertNull(resultado.mediaDuracaoCiclo());
        assertNull(resultado.proximaMenstruacaoEstimada());
        assertNull(resultado.variacaoDuracaoCiclo());

        assertEquals(
                0,
                resultado.quantidadeCiclosAnalisados()
        );

        assertNull(resultado.ovulacaoEstimada());
        assertNull(resultado.inicioJanelaFertilEstimada());
        assertNull(resultado.fimJanelaFertilEstimada());

        assertFalse(resultado.previsaoUltrapassada());

        assertTrue(
                resultado.aviso().contains("Histórico insuficiente")
        );
    }

    @Test
    void naoDeveEstimarOvulacaoComCiclosMuitoVariaveis() {

        List<CicloMenstrual> ciclos = List.of(
                criarCiclo(LocalDate.of(2026, 7, 1)),
                criarCiclo(LocalDate.of(2026, 7, 25)),
                criarCiclo(LocalDate.of(2026, 8, 29)),
                criarCiclo(LocalDate.of(2026, 9, 24))
        );

        when(authClienteService.obterClienteAutenticado())
                .thenReturn(cliente);

        when(cicloRepository.findByClienteIdOrderByDataInicioDesc(
                cliente.getId()
        )).thenReturn(ciclos);

        PrevisaoCicloResponseDTO resultado =
                previsaoService.obterPrevisao();

        assertEquals(28.3, resultado.mediaDuracaoCiclo());
        assertEquals(11, resultado.variacaoDuracaoCiclo());
        assertEquals(3, resultado.quantidadeCiclosAnalisados());

        assertNull(resultado.ovulacaoEstimada());
        assertNull(resultado.inicioJanelaFertilEstimada());
        assertNull(resultado.fimJanelaFertilEstimada());
    }

    @Test
    void naoDeveEstimarOvulacaoPorAltaVariabilidade() {

        LocalDate hoje = LocalDate.now(
                java.time.ZoneId.of("America/Sao_Paulo")
        );

        List<CicloMenstrual> ciclos = List.of(
                criarCiclo(hoje.minusDays(95)),
                criarCiclo(hoje.minusDays(71)),
                criarCiclo(hoje.minusDays(36)),
                criarCiclo(hoje.minusDays(10))
        );

        when(authClienteService.obterClienteAutenticado())
                .thenReturn(cliente);

        when(cicloRepository.findByClienteIdOrderByDataInicioDesc(
                cliente.getId()
        )).thenReturn(ciclos);

        PrevisaoCicloResponseDTO resultado =
                previsaoService.obterPrevisao();

        assertEquals(28.3, resultado.mediaDuracaoCiclo());
        assertEquals(11, resultado.variacaoDuracaoCiclo());
        assertEquals(3, resultado.quantidadeCiclosAnalisados());

        assertFalse(resultado.previsaoUltrapassada());

        assertNull(resultado.ovulacaoEstimada());
        assertNull(resultado.inicioJanelaFertilEstimada());
        assertNull(resultado.fimJanelaFertilEstimada());

        assertTrue(
                resultado.aviso().contains("variação considerável")
        );
    }

    @Test
    void deveIdentificarPrevisaoUltrapassada() {

        LocalDate hoje = LocalDate.now(
                java.time.ZoneId.of("America/Sao_Paulo")
        );

        List<CicloMenstrual> ciclos = List.of(
                criarCiclo(hoje.minusDays(120)),
                criarCiclo(hoje.minusDays(90)),
                criarCiclo(hoje.minusDays(60)),
                criarCiclo(hoje.minusDays(40))
        );

        when(authClienteService.obterClienteAutenticado())
                .thenReturn(cliente);

        when(cicloRepository.findByClienteIdOrderByDataInicioDesc(
                cliente.getId()
        )).thenReturn(ciclos);

        PrevisaoCicloResponseDTO resultado =
                previsaoService.obterPrevisao();

        assertEquals(2, resultado.quantidadeCiclosAnalisados());
        assertTrue(resultado.previsaoUltrapassada());

        assertTrue(
                resultado.proximaMenstruacaoEstimada().isBefore(hoje)
        );

        assertNull(resultado.ovulacaoEstimada());
        assertNull(resultado.inicioJanelaFertilEstimada());
        assertNull(resultado.fimJanelaFertilEstimada());

        assertTrue(
                resultado.aviso().contains("já passou")
        );
    }

}
