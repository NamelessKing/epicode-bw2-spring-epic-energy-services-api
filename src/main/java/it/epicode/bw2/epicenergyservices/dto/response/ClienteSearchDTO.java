package it.epicode.bw2.epicenergyservices.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO minimale per ricerca rapida (autocomplete).
 * Contiene solo i campi essenziali per l'autocompletamento.
 * Usato per: GET /clienti/search?q=rossi&limit=10
 */
public record ClienteSearchDTO(
        @Schema(description = "ID univoco del cliente", example = "1")
        Long id,
        
        @Schema(description = "Ragione sociale / nome azienda", example = "ACME Corporation")
        String ragioneSociale,
        
        @Schema(description = "Partita IVA", example = "12345678901")
        String partitaIva,
        
        @Schema(description = "Email aziendale", example = "info@acme.com")
        String email
) {
}
