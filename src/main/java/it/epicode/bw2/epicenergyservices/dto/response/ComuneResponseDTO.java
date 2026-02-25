package it.epicode.bw2.epicenergyservices.dto.response;

/**
 * DTO di risposta con i dettagli completi di un comune
 * 
 * Include i dati della provincia embedded per evitare chiamate multiple all'API
 * 
 * Usato in:
 * - GET /comuni (lista paginata)
 * - GET /comuni/{id} (dettaglio singolo)
 * - POST /comuni (dopo creazione)
 * - PUT /comuni/{id} (dopo aggiornamento)
 * 
 * Esempio response:
 * {
 *   "id": 1,
 *   "progressivoDelComune": 58091,
 *   "nomeComune": "Roma",
 *   "provincia": {
 *     "id": 58,
 *     "sigla": "RM",
 *     "nome": "Roma",
 *     "regione": "Lazio"
 *   }
 * }
 */
public record ComuneResponseDTO(
    Long id,
    Integer progressivoDelComune,
    String nomeComune,
    ProvinciaEmbeddedDTO provincia
) {
    
    /**
     * DTO nested per i dati essenziali della provincia
     */
    public record ProvinciaEmbeddedDTO(
        Long id,
        String sigla,
        String nome,
        String regione
    ) {}
}
