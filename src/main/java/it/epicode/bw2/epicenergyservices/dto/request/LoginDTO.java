package it.epicode.bw2.epicenergyservices.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO per richiesta di login
 * Inviato a POST /auth/login con credenziali utente
 * 
 * Esempio request:
 * {
 *   "email": "admin@epicenergy.it",
 *   "password": "admin123"
 * }
 * 
 * Validazioni applicate:
 * - email: obbligatoria, formato email valido
 * - password: obbligatoria, almeno 6 caratteri
 */
public record LoginDTO(
    @NotBlank(message = "Email obbligatoria")
    @Email(message = "Email non valida")
    String email,
    
    @NotBlank(message = "Password obbligatoria")
    @Size(min = 6, message = "Password deve essere almeno 6 caratteri")
    String password
) {}
