package it.epicode.bw2.epicenergyservices.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record IndirizziDTO(
        @NotBlank(message = "La via è obbligatoria")
        String via,
        @NotNull(message = "Il civico è obbligatorio")
        @Positive(message = "Il civico deve essere maggiore di 0")
        Long civico,
        @NotBlank(message = "La località è obbligatoria")
        String localita,
        @NotNull(message = "Il CAP è obbligatorio")
        @Positive(message = "Il CAP deve essere maggiore di 0")
        Long cap,
        @NotNull(message = "L'ID del comune è obbligatorio")
        Long comuneId
) {
}
