package it.epicode.bw2.epicenergyservices.exceptions;

import it.epicode.bw2.epicenergyservices.dto.response.ErrorsDTO;
import it.epicode.bw2.epicenergyservices.dto.response.ErrorsWithListDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Global Exception Handler - gestisce tutte le eccezioni lanciate nell'applicazione
 * Ritorna risposte JSON coerenti con status code HTTP appropriati
 */
@RestControllerAdvice
public class ErrorsHandler {
    
    /**
     * 400 - Validation Exception (con lista errori dettagliati)
     * Gestisce ValidationException personalizzata con lista di errori
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorsWithListDTO> handleValidationException(ValidationException ex) {
        ErrorsWithListDTO response = new ErrorsWithListDTO(
                ex.getMessage(),
                LocalDateTime.now(),
                ex.getErrors()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 400 - Spring Validation Error (MethodArgumentNotValidException)
     * Gestisce errori di validazione Bean Validation (@Valid)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorsWithListDTO> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex) {

        BindingResult bindingResult = ex.getBindingResult();
        var errors = bindingResult.getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.toList());

        ErrorsWithListDTO response = new ErrorsWithListDTO(
                "Errori di validazione",
                LocalDateTime.now(),
                errors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 400 - Bad Request Exception
     * Gestisce richieste con dati invalidi
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorsDTO> handleBadRequest(BadRequestException ex) {
        ErrorsDTO response = new ErrorsDTO(
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 401 - Unauthorized Exception
     * Gestisce credenziali errate, token mancante/scaduto
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorsDTO> handleUnauthorized(UnauthorizedException ex) {
        ErrorsDTO response = new ErrorsDTO(
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * 403 - Authorization Denied Exception
     * Gestisce accesso negato per permessi insufficienti
     */

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorsDTO> handleForbidden(AuthorizationDeniedException ex) {
        ErrorsDTO response = new ErrorsDTO(
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    /**
     * 404 - Not Found Exception
     * Gestisce risorsa non trovata nel database
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorsDTO> handleNotFound(NotFoundException ex) {
        ErrorsDTO response = new ErrorsDTO(
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * 409 - Conflict Exception
     * Gestisce conflitti (duplicate entry, race condition, etc)
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorsDTO> handleConflict(ConflictException ex) {
        ErrorsDTO response = new ErrorsDTO(
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /**
     * 500 - Generic Exception
     * Fallback per eccezioni non gestite
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorsDTO> handleGenericException(Exception ex) {
        // Log per debugging (importante!)
        ex.printStackTrace();

        ErrorsDTO response = new ErrorsDTO(
                "C'è stato un errore interno! Contatta l'amministratore.",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * 400 - Invalid Sort/Pagination
     * Gestisce errori di sorting o paginazione invalidi
     */
    @ExceptionHandler(org.springframework.dao.InvalidDataAccessApiUsageException.class)
    public ResponseEntity<ErrorsDTO> handleInvalidDataAccess(
            org.springframework.dao.InvalidDataAccessApiUsageException ex) {
        String message = "Parametri di paginazione o ordinamento non validi. Usa campi validi: ragioneSociale, email, fatturatoAnnuale, dataInserimento, dataUltimoContatto";

        ErrorsDTO response = new ErrorsDTO(
                message,
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
