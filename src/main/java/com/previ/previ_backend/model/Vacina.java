
package com.previ.previ_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "vacinas")
@Getter
@Setter
public class Vacina {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false)
    private Integer dose;

    @Column(name = "data_aplicacao", nullable = false)
    private LocalDate dataAplicacao;

    @Column(name = "proxima_dose")
    private LocalDate proximaDose;

    @Column(name = "local_aplicacao", length = 255)
    private String localAplicacao;

    @Column(columnDefinition = "TEXT")
    private String observacoes;
}
