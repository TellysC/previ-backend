
package com.previ.previ_backend;

import com.previ.previ_backend.dto.CicloMenstrualRequestDTO;
import com.previ.previ_backend.dto.CicloMenstrualResponseDTO;
import com.previ.previ_backend.model.Cliente;
import com.previ.previ_backend.repository.CicloMenstrualRepository;
import com.previ.previ_backend.service.AuthClienteService;
import com.previ.previ_backend.service.CicloMenstrualService;
import com.previ.previ_backend.dto.CicloMenstrualUpdateDTO;
import com.previ.previ_backend.model.CicloMenstrual;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CicloMenstrualServiceTest {

    @Mock
    private CicloMenstrualRepository cicloRepository;

    @Mock
    private AuthClienteService authClienteService;

    @InjectMocks
    private CicloMenstrualService cicloService;

    private Cliente cliente;

    @BeforeEach
    void configurar() {
        cliente = new Cliente();
        cliente.setId(UUID.randomUUID());
    }

    @Test
    void naoDeveCadastrarMenstruacaoSobreposta() {

        CicloMenstrualRequestDTO dto =
                new CicloMenstrualRequestDTO(
                        LocalDate.of(2026, 10, 30),
                        LocalDate.of(2026, 11, 3),
                        "Teste de sobreposição"
                );

        when(authClienteService.obterClienteAutenticado())
                .thenReturn(cliente);

        when(cicloRepository.existeSobreposicao(
                eq(cliente.getId()),
                isNull(),
                eq(dto.dataInicio()),
                eq(dto.dataFim())
        )).thenReturn(true);

        ResponseStatusException excecao = assertThrows(
                ResponseStatusException.class,
                () -> cicloService.cadastrar(dto)
        );

        assertEquals(
                HttpStatus.CONFLICT,
                excecao.getStatusCode()
        );

        verify(cicloRepository, never()).save(any());
    }

    @Test
    void naoDeveAtualizarMenstruacaoSobreposta() {

        UUID cicloId = UUID.randomUUID();

        CicloMenstrual ciclo = new CicloMenstrual();
        ciclo.setId(cicloId);
        ciclo.setCliente(cliente);
        ciclo.setDataInicio(LocalDate.of(2026, 10, 28));
        ciclo.setDataFim(LocalDate.of(2026, 11, 1));

        CicloMenstrualUpdateDTO dto =
                new CicloMenstrualUpdateDTO(
                        LocalDate.of(2026, 9, 30),
                        LocalDate.of(2026, 10, 4),
                        null
                );

        when(authClienteService.obterClienteAutenticado())
                .thenReturn(cliente);

        when(cicloRepository.findByIdAndCliente_Id(
                cicloId, cliente.getId()
        )).thenReturn(Optional.of(ciclo));

        when(cicloRepository.existeSobreposicao(
                eq(cliente.getId()),
                eq(cicloId),
                eq(dto.dataInicio()),
                eq(dto.dataFim())
        )).thenReturn(true);

        ResponseStatusException excecao = assertThrows(
                ResponseStatusException.class,
                () -> cicloService.atualizar(cicloId, dto)
        );

        assertEquals(HttpStatus.CONFLICT, excecao.getStatusCode());

        // Confirma que as datas originais foram preservadas.
        assertEquals(
                LocalDate.of(2026, 10, 28),
                ciclo.getDataInicio()
        );

        assertEquals(
                LocalDate.of(2026, 11, 1),
                ciclo.getDataFim()
        );

        verify(cicloRepository, never()).save(any());
    }

    @Test
    void deveCadastrarMenstruacaoSemSobreposicao() {

        CicloMenstrualRequestDTO dto =
                new CicloMenstrualRequestDTO(
                        LocalDate.of(2026, 10, 10),
                        LocalDate.of(2026, 10, 14),
                        "Menstruação sem conflito"
                );

        when(authClienteService.obterClienteAutenticado())
                .thenReturn(cliente);

        when(cicloRepository.existeSobreposicao(
                eq(cliente.getId()),
                isNull(),
                eq(dto.dataInicio()),
                eq(dto.dataFim())
        )).thenReturn(false);

        when(cicloRepository.save(any(CicloMenstrual.class)))
                .thenAnswer(invocation -> {
                    CicloMenstrual ciclo = invocation.getArgument(0);
                    ciclo.setId(UUID.randomUUID());
                    return ciclo;
                });

        CicloMenstrualResponseDTO resultado =
                cicloService.cadastrar(dto);

        assertEquals(dto.dataInicio(), resultado.dataInicio());
        assertEquals(dto.dataFim(), resultado.dataFim());

        verify(cicloRepository).save(any(CicloMenstrual.class));
    }


    @Test
    void deveAtualizarMenstruacaoSemSobreposicao() {

        UUID cicloId = UUID.randomUUID();

        CicloMenstrual ciclo = new CicloMenstrual();
        ciclo.setId(cicloId);
        ciclo.setCliente(cliente);
        ciclo.setDataInicio(LocalDate.of(2026, 10, 10));
        ciclo.setDataFim(LocalDate.of(2026, 10, 14));
        ciclo.setObservacao("Registro original");

        CicloMenstrualUpdateDTO dto =
                new CicloMenstrualUpdateDTO(
                        LocalDate.of(2026, 10, 11),
                        LocalDate.of(2026, 10, 15),
                        "Registro atualizado"
                );

        when(authClienteService.obterClienteAutenticado())
                .thenReturn(cliente);

        when(cicloRepository.findByIdAndCliente_Id(
                cicloId, cliente.getId()
        )).thenReturn(Optional.of(ciclo));

        when(cicloRepository.existeSobreposicao(
                eq(cliente.getId()),
                eq(cicloId),
                eq(dto.dataInicio()),
                eq(dto.dataFim())
        )).thenReturn(false);

        CicloMenstrualResponseDTO resultado =
                cicloService.atualizar(cicloId, dto);

        assertEquals(dto.dataInicio(), resultado.dataInicio());
        assertEquals(dto.dataFim(), resultado.dataFim());
        assertEquals(dto.observacao(), resultado.observacao());

        verify(cicloRepository).existeSobreposicao(
                cliente.getId(),
                cicloId,
                dto.dataInicio(),
                dto.dataFim()
        );
    }

}
