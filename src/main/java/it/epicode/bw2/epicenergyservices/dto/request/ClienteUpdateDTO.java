package it.epicode.bw2.epicenergyservices.dto.request;

import it.epicode.bw2.epicenergyservices.dto.response.IndirizziDTO;
import it.epicode.bw2.epicenergyservices.entities.TipoAzienda;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

/**
 * DTO per l'aggiornamento di un cliente.
 * Tutti i campi sono OPZIONALI (solo quelli forniti vengono aggiornati).
 * La partita IVA NON può essere modificata (non deve essere presente in questo DTO).
 * Usato per: PUT /clienti/{id}
 */
public record ClienteUpdateDTO(
        @Size(min = 2, max = 255, message = "La ragione sociale deve essere tra 2 e 255 caratteri")
        @Schema(description = "Ragione sociale / nome azienda (opzionale)", example = "ACME Corporation Updated")
        String ragioneSociale,

        @Email(message = "L'indirizzo mail del cliente fornito non è nel formato corretto")
        @Schema(description = "Email aziendale (opzionale)", example = "newemail@acme.com")
        String email,

        @PositiveOrZero(message = "Il fatturato annuale deve essere maggiore o uguale a zero")
        @Schema(description = "Fatturato annuale in euro (opzionale)", example = "200000.00")
        Double fatturatoAnnuale,

        @Email(message = "L'indirizzo pec del cliente fornito non è nel formato corretto")
        @Schema(description = "PEC aziendale (opzionale)", example = "newpec@acme.com")
        String pec,

        @Pattern(regexp = "^[0-9 +()-]{6,20}$", message = "Il numero di telefono del cliente non è valido")
        @Schema(description = "Numero telefono aziendale (opzionale)", example = "+39 0111111111")
        String telefono,

        @Size(max = 500, message = "Il logo aziendale può essere lungo al massimo 500 caratteri")
        @Schema(description = "URL del logo aziendale (opzionale)", example = "https://newlogo.acme.com/logo2.png")
        String logoAziendale,

        @Schema(description = "Tipo di azienda (opzionale)", example = "SRL")
        TipoAzienda tipo,

        @Schema(description = "Indirizzo della sede legale (opzionale)")
        IndirizziDTO sedeLegale,
        
        @Schema(description = "Indirizzo della sede operativa (opzionale)")
        IndirizziDTO sedeOperativa,

        @Schema(description = "ID del comune della sede legale (opzionale)", example = "42")
        Long comuneIdSedeLegale,
        
        @Schema(description = "ID del comune della sede operativa (opzionale)", example = "58")
        Long comuneIdSedeOperativa
) {
}
