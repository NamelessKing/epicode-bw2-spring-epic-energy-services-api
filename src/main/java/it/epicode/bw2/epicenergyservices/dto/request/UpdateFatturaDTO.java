package it.epicode.bw2.epicenergyservices.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record UpdateFatturaDTO(
        @NotNull
        @PastOrPresent
        LocalDate data,
        @Positive
        @Min(0)
        double importo
) {
}
