package it.epicode.bw2.epicenergyservices.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO per l'aggiornamento dei dati del contatto principale del cliente.
 * Usato per: PATCH /clienti/{id}/contatto
 */
public record UpdateContattoDTO(
        @NotBlank(message = "La mail del contatto è un campo obbligatorio")
        @Email(message = "L'indirizzo mail del contatto fornito non è nel formato corretto")
        @Schema(description = "Email del contatto principale", example = "mario.rossi@acme.com")
        String emailContatto,

        @NotBlank(message = "Il nome del contatto è un campo obbligatorio")
        @Size(min = 2, max = 30, message = "Il nome proprio del contatto deve essere tra i 2 e i 30 caratteri")
        @Schema(description = "Nome del contatto", example = "Mario", minLength = 2, maxLength = 30)
        String nomeContatto,

        @NotBlank(message = "Il cognome del contatto è un campo obbligatorio")
        @Size(min = 2, max = 30, message = "Il cognome del contatto deve essere tra i 2 e i 30 caratteri")
        @Schema(description = "Cognome del contatto", example = "Rossi", minLength = 2, maxLength = 30)
        String cognomeContatto,

        @Pattern(
                regexp = "^[0-9 +()-]{6,20}$",
                message = "Il numero di telefono del contatto non è valido"
        )
        @Schema(description = "Telefono del contatto", example = "+39 3201234567")
        String telefonoContatto
) {
}
