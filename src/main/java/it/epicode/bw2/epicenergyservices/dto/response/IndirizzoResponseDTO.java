package it.epicode.bw2.epicenergyservices.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO per la risposta di un indirizzo completo con informazioni del comune.
 * Usato come embedded object in ClienteResponseDTO.
 */
public record IndirizzoResponseDTO(
        @Schema(description = "ID univoco dell'indirizzo", example = "1")
        Long id,
        
        @Schema(description = "Nome della via/strada", example = "Via Roma")
        String via,
        
        @Schema(description = "Numero civico", example = "123")
        String civico,
        
        @Schema(description = "Città/Località", example = "Milano")
        String localita,
        
        @Schema(description = "CAP (Codice di Avviamento Postale)", example = "20100")
        String cap,
        
        @Schema(description = "Informazioni del comune (ID e nome)")
        ComuneEmbeddedDTO comune
) {

    /**
     * DTO embedded per le informazioni minimaliste del comune
     */
    public record ComuneEmbeddedDTO(
            @Schema(description = "ID del comune", example = "42")
            Long id,
            
            @Schema(description = "Nome del comune", example = "Milano")
            String nomeComune
    ) {
    }
}
