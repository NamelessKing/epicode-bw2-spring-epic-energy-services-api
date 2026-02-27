package it.epicode.bw2.epicenergyservices.dto.response;

import it.epicode.bw2.epicenergyservices.entities.TipoAzienda;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

/**
 * DTO minimale per liste di clienti (paginata).
 * Contiene solo i campi essenziali per visualizzare in tabella.
 * Ottimizzato per performance e banda ridotta.
 * Usato per: GET /clienti?page=0&size=20
 */
public record ClienteListItemDTO(
        @Schema(description = "ID univoco del cliente", example = "1")
        Long id,
        
        @Schema(description = "Ragione sociale / nome azienda", example = "ACME Corporation")
        String ragioneSociale,
        
        @Schema(description = "Partita IVA", example = "12345678901")
        String partitaIva,
        
        @Schema(description = "Email aziendale", example = "info@acme.com")
        String email,
        
        @Schema(description = "Fatturato annuale in euro", example = "150000.50")
        Double fatturatoAnnuale,
        
        @Schema(description = "Tipo di azienda", example = "SPA")
        TipoAzienda tipo,
        
        @Schema(description = "Data di inserimento nel sistema", example = "2024-01-15")
        LocalDate dataInserimento,
        
        @Schema(description = "Data dell'ultimo contatto", example = "2024-02-27")
        LocalDate dataUltimoContatto,
        
        @Schema(description = "Sigla provincia della sede legale", example = "MI")
        String nomeProvincia
) {
}
