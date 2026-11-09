package com.previ.previ_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="cliente")
@PrimaryKeyJoinColumn(name="id")
@Getter
@Setter

public class Cliente extends Usuario {

    @Column(name="data_nasc")
    private LocalDate dataNasc;

    @ElementCollection
    @CollectionTable(
            name="cliente_telefone",
            joinColumns = @JoinColumn(name="cliente_id")
    )
    @Column(name="telefone")
    private Set<String> telefone = new HashSet<>();

    public Cliente() {
    }
}
