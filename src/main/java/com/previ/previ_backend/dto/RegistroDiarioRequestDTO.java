
package com.previ.previ_backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record RegistroDiarioRequestDTO(

        @NotNull(message = "A data é obrigatória")
        LocalDate data,

        @Size(max = 255)
        String observacao,

        @Pattern(
                regexp = "LEVE|MODERADO|INTENSO",
                message = "Fluxo inválido"
        )
        String fluxo,

        @Pattern(
                regexp = "LEVE|MODERADA|INTENSA",
                message = "Dor inválida"
        )
        String dor,

        @Pattern(
                regexp = "FELIZ|TRISTE|ANSIOSA|IRRITADA|TRANQUILA",
                message = "Humor inválido"
        )
        String humor,

        Set<UUID> sintomasIds

) {}
