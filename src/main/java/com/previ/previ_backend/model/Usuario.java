package com.previ.previ_backend.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;


import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.JOINED)
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 250)
    private String nome;

    @Column(nullable = false, length = 250, unique = true)
    private String email;

    @Column(nullable = false, length = 250)
    private String senha;

    @CreationTimestamp
    @Column(
            name = "create_in",
            nullable = false,
            updatable = false
    )
    private LocalDateTime dataCriacao;

    protected Usuario() {
    }


    //Getters e Setters
}
