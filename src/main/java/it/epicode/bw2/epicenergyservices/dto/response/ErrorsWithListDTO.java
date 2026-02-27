package it.epicode.bw2.epicenergyservices.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO per risposte di errore con lista di messaggi dettagliati
 * Usato per ValidationException e MethodArgumentNotValidException (errori di validazione)
 * 
 * Esempio response:
 * {
 *   "message": "Errori di validazione",
 *   "timestamp": "2026-02-23T14:30:45.123456",
 *   "errors": [
 *     "Username deve essere tra 3 e 50 caratteri",
 *     "Email non valida",
 *     "Password deve essere almeno 6 caratteri"
 *   ]
 * }
 */
public record ErrorsWithListDTO(
    @Schema(description = "Messaggio di errore generale", example = "Errori di validazione")
    String message,
    
    @Schema(description = "Timestamp dell'errore", example = "2026-02-27T14:30:45.123456")
    LocalDateTime timestamp,
    
    @Schema(description = "Lista dettagliata dei messaggi di errore", example = "[\"La ragione sociale è obbligatoria\", \"L'email non è valida\"]")
    List<String> errors
) {}
