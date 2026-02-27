package it.epicode.bw2.epicenergyservices.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * DTO per risposte di errore semplice (senza lista di errori dettagliati)
 * Usato per eccezioni singole: 401, 403, 404, 409, 500
 * 
 * Esempio response:
 * {
 *   "message": "Risorsa con id 999 non trovata!",
 *   "timestamp": "2026-02-23T14:30:45.123456"
 * }
 */
public record ErrorsDTO(
    @Schema(description = "Messaggio di errore", example = "cliente con id: 999 non trovato.")
    String message,
    
    @Schema(description = "Timestamp dell'errore", example = "2026-02-27T14:30:45.123456")
    LocalDateTime timestamp
) {}
