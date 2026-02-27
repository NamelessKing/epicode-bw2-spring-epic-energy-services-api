package it.epicode.bw2.epicenergyservices.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO per l'aggiornamento del logo aziendale.
 * Usato per: PATCH /clienti/{id}/logo
 */
public record UpdateLogoDTO(
        @NotBlank(message = "L'URL del logo è obbligatorio")
        @Size(max = 500, message = "L'URL del logo può essere lungo al massimo 500 caratteri")
        @Schema(description = "URL del logo aziendale", example = "https://logo.acme.com/newlogo.png", maxLength = 500)
        String logoAziendale
) {
}
