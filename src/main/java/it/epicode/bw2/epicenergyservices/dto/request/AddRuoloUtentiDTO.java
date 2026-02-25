package it.epicode.bw2.epicenergyservices.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO per assegnare un ruolo ad un utente
 * 
 * Usato in PATCH /utenti/ruoli
 * 
 * Esempio request:
 * {
 *   "idUtente": 5,
 *   "idRuolo": 2
 * }
 */
public record AddRuoloUtentiDTO(
        @NotNull(message = "ID utente obbligatorio")
        @Positive(message = "ID utente dev'essere un numero positivo")
        Long idUtente,
        
        @NotNull(message = "ID ruolo obbligatorio")
        @Positive(message = "ID ruolo dev'essere un numero positivo")
        Long idRuolo
) {}
