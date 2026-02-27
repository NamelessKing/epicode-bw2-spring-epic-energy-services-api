package it.epicode.bw2.epicenergyservices.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.epicode.bw2.epicenergyservices.dto.request.FatturaDTO;
import it.epicode.bw2.epicenergyservices.dto.request.StatoFatturaDTO;
import it.epicode.bw2.epicenergyservices.dto.request.UpdateFatturaDTO;
import it.epicode.bw2.epicenergyservices.dto.response.ResponseFatturaDTO;
import it.epicode.bw2.epicenergyservices.dto.response.ErrorsDTO;
import it.epicode.bw2.epicenergyservices.entities.Fattura;
import it.epicode.bw2.epicenergyservices.entities.Utente;
import it.epicode.bw2.epicenergyservices.services.FatturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fatture")
@Tag(name = "Fatture", description = "Gestione fatture clienti - CRUD completo")
@SecurityRequirement(name = "Bearer Authentication")
public class FatturaController {

    private final FatturaService fatturaService;

    @Autowired
    public FatturaController(FatturaService fatturaService) {
        this.fatturaService = fatturaService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea nuova fattura")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Fattura creata con successo"),
        @ApiResponse(responseCode = "400", description = "Dati non validi"),
        @ApiResponse(responseCode = "403", description = "Solo ADMIN")
    })
    public ResponseFatturaDTO saveFattura(
        @RequestBody @Validated FatturaDTO payload,
        @AuthenticationPrincipal Utente utente
    ) {
        Fattura saved = this.fatturaService.save(payload, utente, payload.clienteId());
        return fatturaService.toResponseDTO(saved);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping
    @Operation(summary = "Lista fatture con filtri")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista fatture"),
        @ApiResponse(responseCode = "401", description = "Non autenticato")
    })
    public Page<ResponseFatturaDTO> filtraFatture(
            @RequestParam(required = false) Long idCliente,
            @RequestParam(required = false) String stato,
            @RequestParam(required = false) Integer anno,
            @RequestParam(required = false) Double min,
            @RequestParam(required = false) Double max,
            @PageableDefault(page = 0, size = 10)
            Pageable pageable
    ) {
        return fatturaService.filtraFatture(idCliente, stato, anno, min, max, pageable);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{idFattura}")
    @Operation(summary = "Dettaglio fattura")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Fattura trovata"),
        @ApiResponse(responseCode = "404", description = "Fattura non trovata"),
        @ApiResponse(responseCode = "401", description = "Non autenticato")
    })
    public ResponseFatturaDTO getFattura(@PathVariable Long idFattura) {
        Fattura fattura = this.fatturaService.getFatturaByid(idFattura);
        return fatturaService.toResponseDTO(fattura);
    }

    @PutMapping("/{idFattura}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Modifica fattura completa")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Fattura modificata"),
        @ApiResponse(responseCode = "404", description = "Fattura non trovata"),
        @ApiResponse(responseCode = "400", description = "Dati non validi"),
        @ApiResponse(responseCode = "403", description = "Solo ADMIN")
    })
    public ResponseFatturaDTO updateFattura(
        @PathVariable long idFattura,
        @RequestBody @Validated UpdateFatturaDTO payload
    ) {
        Fattura updated = fatturaService.updateFattura(payload, idFattura);
        return fatturaService.toResponseDTO(updated);
    }

    @PatchMapping("/{idFattura}/stato")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cambia stato fattura")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Stato modificato"),
        @ApiResponse(responseCode = "404", description = "Fattura non trovata"),
        @ApiResponse(responseCode = "400", description = "Stato non valido"),
        @ApiResponse(responseCode = "403", description = "Solo ADMIN")
    })
    public ResponseFatturaDTO updateStatoFattura(
        @PathVariable long idFattura,
        @RequestBody @Validated StatoFatturaDTO payload
    ) {
        Fattura updated = fatturaService.setStatoFattura(idFattura, payload);
        return fatturaService.toResponseDTO(updated);
    }

    @DeleteMapping("/{idFattura}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Elimina fattura")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Fattura eliminata"),
        @ApiResponse(responseCode = "404", description = "Fattura non trovata"),
        @ApiResponse(responseCode = "403", description = "Solo ADMIN")
    })
    public void deleteFattura(@PathVariable long idFattura) {
        fatturaService.deleteFattura(idFattura);
    }
}
