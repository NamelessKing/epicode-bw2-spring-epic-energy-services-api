package it.epicode.bw2.epicenergyservices.dto.response;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record FatturaDTO(
        @NotNull
        @PastOrPresent
        LocalDate data,

        @Positive
        @Min(0)
        double importo,

        @NotBlank(message = "Il numero della fattura è un campo obbligatorio")
        long numero
) {
}
