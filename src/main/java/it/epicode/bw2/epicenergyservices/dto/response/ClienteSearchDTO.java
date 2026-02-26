package it.epicode.bw2.epicenergyservices.dto.response;

/**
 * DTO minimale per ricerca rapida (autocomplete).
 * Contiene solo i campi essenziali per l'autocompletamento.
 * Usato per: GET /clienti/search?q=rossi&limit=10
 */
public record ClienteSearchDTO(
        Long id,
        String ragioneSociale,
        String partitaIva,
        String email
) {
}
