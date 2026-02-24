package it.epicode.bw2.epicenergyservices.dto.response;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateContattoDTO(
        @NotBlank(message = "La mail del contatto è un campo obbligatorio")
        @Email(message = "L'indirizzo mail del contatto fornito non è nel formato corretto")
        String emailContatto,

        @NotBlank(message = "Il nome del contatto è un campo obbligatorio")
        @Size(min = 2, max = 30, message = "Il nome proprio del contatto deve essere tra i 2 e i 30 caratteri")
        String nomeContatto,

        @NotBlank(message = "Il cognome del contatto è un campo obbligatorio")
        @Size(min = 2, max = 30, message = "Il cognome del contatto deve essere tra i 2 e i 30 caratteri")
        String cognomeContatto,

        @Pattern(
                regexp = "^[0-9 +()-]{6,20}$",
                message = "Il numero di telefono del contatto non è valido"
        )
        String telefonoContatto
) {
}
