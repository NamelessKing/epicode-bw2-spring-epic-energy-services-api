package it.epicode.bw2.epicenergyservices.exceptions;

/**
 * Eccezione lanciata quando una risorsa non è trovata nel database (404 Not Found)
 * Esempi: user con id=999 non esiste, cliente non trovato
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
    
    public NotFoundException(Long id) {
        super("Risorsa con id " + id + " non trovata!");
    }
}
