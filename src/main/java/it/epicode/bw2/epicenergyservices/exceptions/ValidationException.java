package it.epicode.bw2.epicenergyservices.exceptions;

import java.util.List;

/**
 * Eccezione lanciata quando ci sono errori di validazione (400 Bad Request con lista errori)
 * Contiene una lista di messaggi di errore dettagliati
 */
public class ValidationException extends RuntimeException {
    private final List<String> errors;
    
    public ValidationException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }
    
    public List<String> getErrors() {
        return errors;
    }
}
