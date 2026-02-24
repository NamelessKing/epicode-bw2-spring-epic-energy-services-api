package it.epicode.bw2.epicenergyservices.dto.response;

import it.epicode.bw2.epicenergyservices.entities.TipoAzienda;
import jakarta.validation.constraints.*;

public record ClienteDTO(
        @NotBlank(message = "La ragione sociale è un campo obbligatorio")
        @Size(min = 2, max = 30, message = "La ragione sociale deve essere tra i 2 e i 30 caratteri")
        String ragioneSociale,

        @NotBlank(message = "La partita iva è un campo obbligatorio")
        @Size(min = 11, max = 11, message = "La partita iva deve essere di 11 caratteri")
        String partitaIva,

        @NotBlank(message = "La mail del cliente è un campo obbligatorio")
        @Email(message = "L'indirizzo mail del cliente fornito non è nel formato corretto")
        String email,

        @Positive(message = "Il fatturato annuale deve essere maggiore di zero")
        double fatturatoAnnuale,

        @NotBlank(message = "La pec del cliente è un campo obbligatorio")
        @Email(message = "L'indirizzo pec del cliente fornito non è nel formato corretto")
        String pec,

        @Pattern(
                regexp = "^[0-9 +()-]{6,20}$",
                message = "Il numero di telefono del cliente non è valido"
        )
        String telefono,

        @NotNull(message = "Il tipo di azienda è obbligatorio")
        TipoAzienda tipo,

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
