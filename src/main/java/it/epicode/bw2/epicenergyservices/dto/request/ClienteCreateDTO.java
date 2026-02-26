package it.epicode.bw2.epicenergyservices.dto.request;

import it.epicode.bw2.epicenergyservices.dto.response.IndirizziDTO;
import it.epicode.bw2.epicenergyservices.entities.TipoAzienda;
import jakarta.validation.constraints.*;

/**
 * DTO per la creazione di un nuovo cliente.
 * Contiene tutti i dati richiesti per creare un cliente con indirizzi.
 * Usato per: POST /clienti
 */
public record ClienteCreateDTO(
        @NotBlank(message = "La ragione sociale è un campo obbligatorio")
        @Size(min = 2, max = 255, message = "La ragione sociale deve essere tra 2 e 255 caratteri")
        String ragioneSociale,

        @NotBlank(message = "La partita iva è un campo obbligatorio")
        @Size(min = 11, max = 11, message = "La partita iva deve essere di 11 caratteri")
        String partitaIva,

        @NotBlank(message = "La mail del cliente è un campo obbligatorio")
        @Email(message = "L'indirizzo mail del cliente fornito non è nel formato corretto")
        String email,

        @NotNull(message = "Il fatturato annuale è obbligatorio")
        @PositiveOrZero(message = "Il fatturato annuale deve essere maggiore o uguale a zero")
        Double fatturatoAnnuale,

        @NotBlank(message = "La pec del cliente è un campo obbligatorio")
        @Email(message = "L'indirizzo pec del cliente fornito non è nel formato corretto")
        String pec,

        @Pattern(regexp = "^[0-9 +()-]{6,20}$", message = "Il numero di telefono del cliente non è valido")
        String telefono,

        @Size(max = 500, message = "Il logo aziendale può essere lungo al massimo 500 caratteri")
        String logoAziendale,

        @NotNull(message = "Il tipo di azienda è obbligatorio")
        TipoAzienda tipo,

        @NotBlank(message = "La mail del contatto è un campo obbligatorio")
        @Email(message = "L'indirizzo mail del contatto fornito non è nel formato corretto")
        String emailContatto,

        @NotBlank(message = "Il nome del contatto è un campo obbligatorio")
        @Size(min = 2, max = 50, message = "Il nome proprio del contatto deve essere tra 2 e 50 caratteri")
        String nomeContatto,

        @NotBlank(message = "Il cognome del contatto è un campo obbligatorio")
        @Size(min = 2, max = 50, message = "Il cognome del contatto deve essere tra 2 e 50 caratteri")
        String cognomeContatto,

        @NotBlank(message = "Il numero di telefono del contatto è un campo obbligatorio")
        @Pattern(regexp = "^[0-9 +()-]{6,20}$", message = "Il numero di telefono del contatto non è valido")
        String telefonoContatto,

        @NotNull(message = "La sede legale è obbligatoria")
        IndirizziDTO sedeLegale,

        IndirizziDTO sedeOperativa,

        @NotNull(message = "Il comune della sede legale è obbligatorio")
        Long comuneIdSedeLegale,

        Long comuneIdSedeOperativa
) {
}
