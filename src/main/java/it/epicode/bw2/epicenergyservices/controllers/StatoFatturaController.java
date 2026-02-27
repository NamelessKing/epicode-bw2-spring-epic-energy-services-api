package it.epicode.bw2.epicenergyservices.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.epicode.bw2.epicenergyservices.dto.request.StatoFatturaDTO;
import it.epicode.bw2.epicenergyservices.dto.response.ErrorsDTO;
import it.epicode.bw2.epicenergyservices.entities.StatoFattura;
import it.epicode.bw2.epicenergyservices.services.StatoFatturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stati-fattura")
@Tag(name = "Stati Fattura", description = "Gestione stati fattura dinamici - ADMIN only")
@SecurityRequirement(name = "Bearer Authentication")
public class StatoFatturaController {

    private final StatoFatturaService statoFatturaService;

    @Autowired
    public StatoFatturaController(StatoFatturaService statoFatturaService) {
        this.statoFatturaService = statoFatturaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crea nuovo stato fattura")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Stato creato con successo"),
        @ApiResponse(responseCode = "400", description = "Dati non validi"),
        @ApiResponse(responseCode = "403", description = "Accesso negato - Solo ADMIN")
    })
    public StatoFatturaDTO addStatoFattura(@RequestBody @Validated StatoFatturaDTO payload) {
        StatoFattura statoFattura = statoFatturaService.save(payload);
        return this.statoFatturaService.toResponseDTO(statoFattura);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Lista tutti gli stati fattura")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista stati recuperata"),
        @ApiResponse(responseCode = "401", description = "Non autenticato"),
        @ApiResponse(responseCode = "403", description = "Accesso negato")
    })
    public List<StatoFatturaDTO> getAllStatiFattura() {
        return statoFatturaService.findAll()
            .stream()
            .map(statoFatturaService::toResponseDTO)
            .toList();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Modifica stato fattura")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Stato modificato con successo"),
        @ApiResponse(responseCode = "404", description = "Stato non trovato"),
        @ApiResponse(responseCode = "400", description = "Dati non validi"),
        @ApiResponse(responseCode = "403", description = "Accesso negato - Solo ADMIN")
    })
    public StatoFatturaDTO updateStatoFattura(
        @PathVariable long id,
        @RequestBody @Validated StatoFatturaDTO payload
    ) {
        StatoFattura updated = statoFatturaService.updateStatoFattura(id, payload);
        return statoFatturaService.toResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Elimina stato fattura")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Stato eliminato"),
        @ApiResponse(responseCode = "404", description = "Stato non trovato"),
        @ApiResponse(responseCode = "403", description = "Accesso negato - Solo ADMIN")
    })
    public void deleteStatoFattura(@PathVariable long id) {
        statoFatturaService.eliminaStatoFatturaConId(id);
    }
}
