package it.epicode.bw2.epicenergyservices.exceptions;

/**
 * Eccezione lanciata quando una richiesta contiene dati invalidi (400 Bad Request)
 * Esempi: email già in uso, username duplicato, validazione fallita
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
