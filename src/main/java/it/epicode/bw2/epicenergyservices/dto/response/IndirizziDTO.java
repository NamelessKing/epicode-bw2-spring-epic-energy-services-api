package it.epicode.bw2.epicenergyservices.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record IndirizziDTO(
        @NotBlank(message = "La via è obbligatoria")
        String via,
        @NotNull(message = "Il civico è obbligatorio")
        String civico,
        @NotBlank(message = "La località è obbligatoria")
        String localita,
        @NotNull(message = "Il CAP è obbligatorio")
        String cap,
        @NotNull(message = "Il comune è obbligatorio")
        Long comuneId
) {
}
