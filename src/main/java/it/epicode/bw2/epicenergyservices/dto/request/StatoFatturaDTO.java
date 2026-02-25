package it.epicode.bw2.epicenergyservices.dto.request;

import jakarta.validation.constraints.NotBlank;

public record StatoFatturaDTO(
        @NotBlank(message = "Lo stato fattura è un campo obbligatorio")
        String stato
) {
}
