package it.epicode.bw2.epicenergyservices.dto.request;

import it.epicode.bw2.epicenergyservices.dto.response.IndirizziDTO;
import it.epicode.bw2.epicenergyservices.entities.TipoAzienda;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

/**
 * DTO per la creazione di un nuovo cliente.
 * Contiene tutti i dati richiesti per creare un cliente con indirizzi.
 * Usato per: POST /clienti
 */
public record ClienteCreateDTO(
        @NotBlank(message = "La ragione sociale è un campo obbligatorio")
        @Size(min = 2, max = 255, message = "La ragione sociale deve essere tra 2 e 255 caratteri")
        @Schema(description = "Ragione sociale / nome azienda", example = "ACME Corporation", minLength = 2, maxLength = 255)
        String ragioneSociale,

        @NotBlank(message = "La partita iva è un campo obbligatorio")
        @Size(min = 11, max = 11, message = "La partita iva deve essere di 11 caratteri")
        @Schema(description = "Partita IVA (esattamente 11 cifre)", example = "12345678901", minLength = 11, maxLength = 11)
        String partitaIva,

        @NotBlank(message = "La mail del cliente è un campo obbligatorio")
        @Email(message = "L'indirizzo mail del cliente fornito non è nel formato corretto")
        @Schema(description = "Email aziendale valida", example = "info@acme.com")
        String email,

        @NotNull(message = "Il fatturato annuale è obbligatorio")
        @PositiveOrZero(message = "Il fatturato annuale deve essere maggiore o uguale a zero")
        @Schema(description = "Fatturato annuale in euro (>= 0)", example = "150000.50")
        Double fatturatoAnnuale,

        @NotBlank(message = "La pec del cliente è un campo obbligatorio")
        @Email(message = "L'indirizzo pec del cliente fornito non è nel formato corretto")
        @Schema(description = "PEC aziendale valida", example = "pec@acme.com")
        String pec,

        @Pattern(regexp = "^[0-9 +()-]{6,20}$", message = "Il numero di telefono del cliente non è valido")
        @Schema(description = "Numero telefono aziendale (6-20 caratteri)", example = "+39 0123456789")
        String telefono,

        @Size(max = 500, message = "Il logo aziendale può essere lungo al massimo 500 caratteri")
        @Schema(description = "URL del logo aziendale (max 500 car)", example = "https://logo.acme.com/logo.png", maxLength = 500)
        String logoAziendale,

        @NotNull(message = "Il tipo di azienda è obbligatorio")
        @Schema(description = "Tipo di azienda", example = "SPA")
        TipoAzienda tipo,

        @NotBlank(message = "La mail del contatto è un campo obbligatorio")
        @Email(message = "L'indirizzo mail del contatto fornito non è nel formato corretto")
        @Schema(description = "Email del contatto principale", example = "mario@acme.com")
        String emailContatto,

        @NotBlank(message = "Il nome del contatto è un campo obbligatorio")
        @Size(min = 2, max = 50, message = "Il nome proprio del contatto deve essere tra 2 e 50 caratteri")
        @Schema(description = "Nome del contatto", example = "Mario", minLength = 2, maxLength = 50)
        String nomeContatto,

        @NotBlank(message = "Il cognome del contatto è un campo obbligatorio")
        @Size(min = 2, max = 50, message = "Il cognome del contatto deve essere tra 2 e 50 caratteri")
        @Schema(description = "Cognome del contatto", example = "Rossi", minLength = 2, maxLength = 50)
        String cognomeContatto,

        @NotBlank(message = "Il numero di telefono del contatto è un campo obbligatorio")
        @Pattern(regexp = "^[0-9 +()-]{6,20}$", message = "Il numero di telefono del contatto non è valido")
        @Schema(description = "Telefono del contatto", example = "+39 3201234567")
        String telefonoContatto,

        @NotNull(message = "La sede legale è obbligatoria")
        @Schema(description = "Indirizzo della sede legale")
        IndirizziDTO sedeLegale,

        @Schema(description = "Indirizzo della sede operativa (opzionale)")
        IndirizziDTO sedeOperativa,

        @NotNull(message = "Il comune della sede legale è obbligatorio")
        @Schema(description = "ID del comune della sede legale", example = "42")
        Long comuneIdSedeLegale,

        @Schema(description = "ID del comune della sede operativa (opzionale)", example = "58")
        Long comuneIdSedeOperativa
) {
}
