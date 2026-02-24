package it.epicode.bw2.epicenergyservices.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ComuneDTO(

        @Positive(message = "Il progressivo deve essere maggiore di 0")
        int progressivoDelComune,

        @NotBlank(message = "Il nome comune è obbligatorio")
        String nomeComune,

        @NotNull(message = "provinciaId è obbligatorio")
        Long provinciaId
) {}