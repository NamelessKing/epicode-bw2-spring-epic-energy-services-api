package it.epicode.bw2.epicenergyservices.dto.response;

import it.epicode.bw2.epicenergyservices.entities.TipoAzienda;
import java.time.LocalDate;

/**
 * DTO per la risposta completa di un cliente (dettaglio).
 * Contiene tutti i dettagli del cliente inclusi gli indirizzi completi con informazioni comuni.
 * Usato per: GET /clienti/{id}
 */
public record ClienteResponseDTO(
        Long id,
        Boolean cancellato,
        String ragioneSociale,
        String partitaIva,
        String email,
        Double fatturatoAnnuale,
        String pec,
        String telefono,
        String logoAziendale,
        TipoAzienda tipo,
        LocalDate dataInserimento,
        LocalDate dataUltimoContatto,
        String emailContatto,
        String nomeContatto,
        String cognomeContatto,
        String telefonoContatto,
        IndirizzoResponseDTO sedeLegale,
        IndirizzoResponseDTO sedeOperativa
) {
}
