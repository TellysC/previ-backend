package com.previ.previ_backend.model;

import com.previ.previ_backend.enums.StatusExame;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table (name = "exames")
@Getter
@Setter
public class Exame {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Column(name = "data", nullable = false)
    private LocalDate data;

    @Column(name = "hora")
    private LocalTime hora;

    @Column(name = "data_realizacao")
    private LocalDate dataRealizacao;

    @Column(name = "local", length = 255)
    private String local;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusExame status = StatusExame.AGENDADO;

    @Column(name = "observacao", length = 255)
    private String observacao;

    public Exame() {
    }

}
