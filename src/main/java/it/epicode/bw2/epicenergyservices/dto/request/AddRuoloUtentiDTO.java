package it.epicode.bw2.epicenergyservices.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record AddRuoloUtentiDTO(
        @NotBlank(message = "ID utente obbligatorio")
        @Positive(message = "ID utente dev'essere un numero positivo")
        Long idUtente,
        @NotBlank(message = "ID ruolo obbligatorio")
        @Positive(message = "ID ruolo dev'essere un numero positivo")
        Long idRuolo
) {
}
