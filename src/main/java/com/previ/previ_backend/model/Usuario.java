package com.previ.previ_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;


import java.util.UUID;

@Entity
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public class  Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String nome;

    @Column(nullable = false, length = 255, unique = true)
    private String email;

    @Column(nullable = false, length = 255)
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

    @Column(name= "deleted_at")
    private LocalDateTime deletedAt;

    //Getters e Setters
}
