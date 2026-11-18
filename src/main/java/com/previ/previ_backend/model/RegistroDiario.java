
package com.previ.previ_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "registro_diario")
@Getter
@Setter
public class RegistroDiario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(nullable = false)
    private LocalDate data;

    @Column(length = 255)
    private String observacao;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "registro_diario_sintoma",
            joinColumns = @JoinColumn(name = "registro_diario_id"),
            inverseJoinColumns = @JoinColumn(name = "sintoma_id")
    )
    private Set<Sintoma> sintomas = new HashSet<>();

    @Column(length = 20)
    private String fluxo;

    @Column(length = 20)
    private String dor;

    @Column(length = 20)
    private String humor;
}
