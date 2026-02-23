package it.epicode.bw2.epicenergyservices.exceptions;

/**
 * Eccezione lanciata quando l'utente è autenticato ma non ha i permessi richiesti (403 Forbidden)
 * Esempi: utente USER tenta di eliminare un cliente (operazione solo ADMIN)
 */
public class AuthorizationDeniedException extends RuntimeException {
    public AuthorizationDeniedException(String message) {
        super(message);
    }
}
