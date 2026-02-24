package it.epicode.bw2.epicenergyservices.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RuoliDTO(
        @NotBlank(message = "Ruolo obbligatorio")
        @Size(min = 2, max = 50, message = "Il ruolo deve essere tra 2 e 50 caratteri")
        String ruolo
) {
}
