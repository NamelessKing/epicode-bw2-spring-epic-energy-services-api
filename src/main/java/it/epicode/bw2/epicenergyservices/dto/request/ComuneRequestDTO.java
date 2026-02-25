package it.epicode.bw2.epicenergyservices.dto.request;

import jakarta.validation.constraints.*;

/**
 * DTO per creare o aggiornare un comune
 * 
 * Usato in:
 * - POST /comuni (crea nuovo comune)
 * - PUT /comuni/{id} (aggiorna comune esistente)
 * 
 * Esempio request:
 * {
 *   "progressivoDelComune": 58091,
 *   "nomeComune": "Roma",
 *   "provinciaId": 58
 * }
 */
public record ComuneRequestDTO(
    
    @NotNull(message = "Il progressivo del comune è obbligatorio")
    @Positive(message = "Il progressivo deve essere maggiore di 0")
    Integer progressivoDelComune,
    
    @NotBlank(message = "Il nome del comune è obbligatorio")
    @Size(min = 2, max = 100, message = "Il nome deve essere tra 2 e 100 caratteri")
    String nomeComune,
    
    @NotNull(message = "L'ID della provincia è obbligatorio")
    @Positive(message = "L'ID della provincia deve essere maggiore di 0")
    Long provinciaId
) {
    /**
     * Compact constructor per normalizzazione automatica
     */
    public ComuneRequestDTO {
        if (nomeComune != null) {
            nomeComune = nomeComune.trim();
        }
    }
}
