package it.epicode.bw2.epicenergyservices.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record IndirizzoRequestDTO(

        @NotBlank(message = "La via è obbligatoria")
        @Size(min = 2, max = 100, message = "La via deve essere tra 2 e 100 caratteri")
        String via,

        @NotBlank(message = "Il civico è obbligatorio")
        String civico,

        @NotBlank(message = "La località è obbligatoria")
        String localita,

        @NotBlank(message = "Il CAP è obbligatorio")
        @Size(min = 5, max = 5, message = "Il CAP deve essere di 5 cifre")
        String cap,

        @NotNull(message = "L'ID del comune è obbligatorio")
        Long comuneId
) {
    public IndirizzoRequestDTO {
        if (via != null) via = via.trim();
        if (localita != null) localita = localita.trim();
        if (cap != null) cap = cap.trim();
    }
}
