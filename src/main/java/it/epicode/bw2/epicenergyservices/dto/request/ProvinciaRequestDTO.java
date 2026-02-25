package it.epicode.bw2.epicenergyservices.dto.request;

import jakarta.validation.constraints.*;

/**
 * DTO per creare o aggiornare una provincia
 * 
 * Usato in:
 * - POST /province (crea nuova provincia)
 * - PUT /province/{id} (aggiorna provincia esistente)
 * 
 * Esempio request:
 * {
 *   "sigla": "RM",
 *   "provincia": "Roma",
 *   "regione": "Lazio"
 * }
 */
public record ProvinciaRequestDTO(
    
    @NotBlank(message = "La sigla della provincia è obbligatoria")
    @Size(min = 2, max = 2, message = "La sigla deve essere di 2 caratteri")
    @Pattern(regexp = "^[A-Z]{2}$", message = "La sigla deve essere di 2 lettere maiuscole")
    String sigla,
    
    @NotBlank(message = "Il nome della provincia è obbligatorio")
    @Size(min = 2, max = 100, message = "Il nome deve essere tra 2 e 100 caratteri")
    String provincia,
    
    @NotBlank(message = "La regione è obbligatoria")
    @Size(min = 2, max = 100, message = "La regione deve essere tra 2 e 100 caratteri")
    String regione
) {
    /**
     * Compact constructor per normalizzazione automatica
     */
    public ProvinciaRequestDTO {
        if (sigla != null) {
            sigla = sigla.trim().toUpperCase();
        }
        if (provincia != null) {
            provincia = provincia.trim();
        }
        if (regione != null) {
            regione = regione.trim();
        }
    }
}
