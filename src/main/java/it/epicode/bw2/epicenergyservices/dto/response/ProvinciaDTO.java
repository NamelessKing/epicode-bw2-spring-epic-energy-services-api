package it.epicode.bw2.epicenergyservices.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProvinciaDTO(

        @NotBlank(message = "La sigla è obbligatoria")
        @Size(min = 2, max = 2, message = "La sigla deve essere di 2 caratteri")
        String sigla,

        @NotBlank(message = "Il nome provincia è obbligatorio")
        String provincia,

        @NotBlank(message = "La regione è obbligatoria")
        String regione
) {
}