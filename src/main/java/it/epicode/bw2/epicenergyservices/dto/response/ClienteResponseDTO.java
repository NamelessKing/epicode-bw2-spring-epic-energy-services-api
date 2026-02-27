package it.epicode.bw2.epicenergyservices.dto.response;

import it.epicode.bw2.epicenergyservices.entities.TipoAzienda;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

/**
 * DTO per la risposta completa di un cliente (dettaglio).
 * Contiene tutti i dettagli del cliente inclusi gli indirizzi completi con informazioni comuni.
 * Usato per: GET /clienti/{id}
 */
public record ClienteResponseDTO(
        @Schema(description = "ID univoco del cliente", example = "1")
        Long id,
        
        @Schema(description = "Flag soft delete - true se cancellato", example = "false")
        Boolean cancellato,
        
        @Schema(description = "Ragione sociale / nome azienda", example = "ACME Corporation")
        String ragioneSociale,
        
        @Schema(description = "Partita IVA (11 cifre)", example = "12345678901")
        String partitaIva,
        
        @Schema(description = "Email aziendale", example = "info@acme.com")
        String email,
        
        @Schema(description = "Fatturato annuale in euro", example = "150000.50")
        Double fatturatoAnnuale,
        
        @Schema(description = "PEC (Posta Elettronica Certificata)", example = "pec@acme.com")
        String pec,
        
        @Schema(description = "Numero di telefono aziendale", example = "+39 0123456789")
        String telefono,
        
        @Schema(description = "URL del logo aziendale", example = "https://logo.acme.com/logo.png")
        String logoAziendale,
        
        @Schema(description = "Tipo di azienda", example = "SPA")
        TipoAzienda tipo,
        
        @Schema(description = "Data di inserimento nel sistema", example = "2024-01-15")
        LocalDate dataInserimento,
        
        @Schema(description = "Data dell'ultimo contatto con il cliente", example = "2024-02-27")
        LocalDate dataUltimoContatto,
        
        @Schema(description = "Email del contatto principale", example = "mario@acme.com")
        String emailContatto,
        
        @Schema(description = "Nome del contatto principale", example = "Mario")
        String nomeContatto,
        
        @Schema(description = "Cognome del contatto principale", example = "Rossi")
        String cognomeContatto,
        
        @Schema(description = "Telefono del contatto principale", example = "+39 3201234567")
        String telefonoContatto,
        
        @Schema(description = "Indirizzo della sede legale con dettagli comune")
        IndirizzoResponseDTO sedeLegale,
        
        @Schema(description = "Indirizzo della sede operativa con dettagli comune")
        IndirizzoResponseDTO sedeOperativa
) {
}
