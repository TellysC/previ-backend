package com.previ.previ_backend.dto;
import com.previ.previ_backend.enums.StatusExame;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;

public record ExameUpdateDTO(

        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 150)
        String nome,

        @NotNull(message = "A Data é obrigatório")
        LocalDate data,

        LocalTime hora,

        LocalDate dataRealizacao,

        @Size(max = 255)
        String local,

        @NotNull(message = "O Status é obrigatório")
        StatusExame status,

        @Size(max = 255)
        String observacao

) {}
