package it.epicode.bw2.epicenergyservices.exceptions;

/**
 * Eccezione lanciata quando credenziali sono errate o token è mancante/scaduto (401 Unauthorized)
 * Esempi: password sbagliata, token assente, token scaduto
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
