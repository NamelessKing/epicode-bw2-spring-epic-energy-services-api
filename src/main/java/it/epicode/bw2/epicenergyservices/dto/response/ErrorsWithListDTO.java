package it.epicode.bw2.epicenergyservices.dto.response;

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
    String message,
    LocalDateTime timestamp,
    List<String> errors
) {}
