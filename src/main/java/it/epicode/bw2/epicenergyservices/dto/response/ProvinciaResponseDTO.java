package it.epicode.bw2.epicenergyservices.dto.response;

/**
 * DTO di risposta con i dettagli completi di una provincia
 * 
 * Include l'ID per permettere riferimenti e aggiornamenti
 * 
 * Usato in:
 * - GET /province (lista paginata)
 * - GET /province/{id} (dettaglio singolo)
 * - POST /province (dopo creazione)
 * - PUT /province/{id} (dopo aggiornamento)
 * 
 * Esempio response:
 * {
 *   "id": 58,
 *   "sigla": "RM",
 *   "provincia": "Roma",
 *   "regione": "Lazio"
 * }
 */
public record ProvinciaResponseDTO(
    Long id,
    String sigla,
    String provincia,
    String regione
) {}
