package it.epicode.bw2.epicenergyservices.dto.response;

import it.epicode.bw2.epicenergyservices.entities.TipoAzienda;
import java.time.LocalDate;

/**
 * DTO minimale per liste di clienti (paginata).
 * Contiene solo i campi essenziali per visualizzare in tabella.
 * Ottimizzato per performance e banda ridotta.
 * Usato per: GET /clienti?page=0&size=20
 */
public record ClienteListItemDTO(
        Long id,
        String ragioneSociale,
        String partitaIva,
        String email,
        Double fatturatoAnnuale,
        TipoAzienda tipo,
        LocalDate dataInserimento,
        LocalDate dataUltimoContatto,
        String nomeProvincia
) {
}
