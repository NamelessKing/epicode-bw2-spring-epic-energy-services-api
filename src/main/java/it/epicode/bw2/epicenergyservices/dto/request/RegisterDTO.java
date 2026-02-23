package it.epicode.bw2.epicenergyservices.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO per richiesta di registrazione nuovo utente
 * Inviato a POST /auth/register
 * 
 * Esempio request:
 * {
 *   "username": "mario_rossi",
 *   "email": "mario@epicenergy.it",
 *   "password": "securePass123",
 *   "role": "USER",
 *   "firstName": "Mario",
 *   "lastName": "Rossi",
 *   "avatarUrl": "https://api.example.com/avatars/mario.jpg"
 * }
 * 
 * Validazioni applicate:
 * - username: obbligatorio, 3-50 caratteri
 * - email: obbligatoria, formato email valido
 * - password: obbligatoria, almeno 6 caratteri
 * - role: obbligatorio, deve essere USER o ADMIN
 * - firstName, lastName, avatarUrl: opzionali
 */
public record RegisterDTO(
    @NotBlank(message = "Username obbligatorio")
    @Size(min = 3, max = 50, message = "Username deve essere tra 3 e 50 caratteri")
    String username,
    
    @NotBlank(message = "Email obbligatoria")
    @Email(message = "Email non valida")
    String email,
    
    @NotBlank(message = "Password obbligatoria")
    @Size(min = 6, message = "Password deve essere almeno 6 caratteri")
    String password,
    
    @Pattern(regexp = "USER|ADMIN", message = "Ruolo deve essere USER o ADMIN")
    String role,
    
    String firstName,
    String lastName,
    String avatarUrl
) {}
