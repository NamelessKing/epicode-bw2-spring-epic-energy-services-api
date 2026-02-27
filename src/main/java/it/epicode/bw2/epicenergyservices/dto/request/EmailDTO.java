package it.epicode.bw2.epicenergyservices.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EmailDTO(
        @NotBlank(message = "Soggetto email obbligatorio")
        @Size(min = 5, max = 200, message = "Soggetto tra 5 e 200 caratteri")
        String soggetto,

        @NotBlank(message = "Corpo email obbligatorio")
        @Size(min = 10, max = 5000, message = "Corpo tra 10 e 5000 caratteri")
        String corpo
) {
}
