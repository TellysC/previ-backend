package com.previ.previ_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name  = "administrador")
@PrimaryKeyJoinColumn(name = "id")

public class Administrador extends Usuario {

    protected Administrador() {
    }
}
