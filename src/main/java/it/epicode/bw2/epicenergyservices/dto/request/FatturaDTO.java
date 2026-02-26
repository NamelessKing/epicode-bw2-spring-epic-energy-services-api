package it.epicode.bw2.epicenergyservices.dto.request;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record FatturaDTO(
        @NotNull
        @PastOrPresent
        LocalDate data,

        @Positive
        @Min(0)
        double importo,

        @NotNull
        @Positive
        Long numero
) {
}
