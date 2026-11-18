
package com.previ.previ_backend.model;

import com.previ.previ_backend.enums.StatusConsulta;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "consultas")
@Getter
@Setter
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(nullable = false, length = 150)
    private String especialidade;

    @Column(length = 150)
    private String profissional;

    @Column(nullable = false)
    private LocalDate data;

    private LocalTime hora;

    @Column(name = "data_realizacao")
    private LocalDate dataRealizacao;

    @Column(length = 255)
    private String local;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusConsulta status = StatusConsulta.AGENDADA;

    @Column(length = 255)
    private String observacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consulta_origem_id")
    private Consulta consultaOrigem;
}
