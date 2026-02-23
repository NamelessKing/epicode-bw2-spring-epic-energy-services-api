package it.epicode.bw2.epicenergyservices.exceptions;

/**
 * Eccezione lanciata quando c'è un conflitto (409 Conflict)
 * Esempi: email duplicata, race condition, posti esauriti in una fattura
 */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
