package it.epicode.bw2.epicenergyservices.dto.request;

import it.epicode.bw2.epicenergyservices.dto.response.IndirizziDTO;
import it.epicode.bw2.epicenergyservices.entities.TipoAzienda;
import jakarta.validation.constraints.*;

/**
 * DTO per l'aggiornamento di un cliente.
 * Tutti i campi sono OPZIONALI (solo quelli forniti vengono aggiornati).
 * La partita IVA NON può essere modificata (non deve essere presente in questo DTO).
 * Usato per: PUT /clienti/{id}
 */
public record ClienteUpdateDTO(
        @Size(min = 2, max = 255, message = "La ragione sociale deve essere tra 2 e 255 caratteri")
        String ragioneSociale,

        @Email(message = "L'indirizzo mail del cliente fornito non è nel formato corretto")
        String email,

        @PositiveOrZero(message = "Il fatturato annuale deve essere maggiore o uguale a zero")
        Double fatturatoAnnuale,

        @Email(message = "L'indirizzo pec del cliente fornito non è nel formato corretto")
        String pec,

        @Pattern(regexp = "^[0-9 +()-]{6,20}$", message = "Il numero di telefono del cliente non è valido")
        String telefono,

        @Size(max = 500, message = "Il logo aziendale può essere lungo al massimo 500 caratteri")
        String logoAziendale,

        TipoAzienda tipo,

        IndirizziDTO sedeLegale,
        IndirizziDTO sedeOperativa,

        Long comuneIdSedeLegale,
        Long comuneIdSedeOperativa
) {
}
