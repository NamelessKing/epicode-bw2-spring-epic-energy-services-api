package it.epicode.bw2.epicenergyservices.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AddStatoFatturaDTO(
        @NotNull(message = "ID fattura obbligatorio")
        @Positive(message = "ID fattura dev'essere un numero positivo")
        Long idFattura,

        @NotNull(message = "ID stato obbligatorio")
        @Positive(message = "ID stato dev'essere un numero positivo")
        Long idStato
) {
}
