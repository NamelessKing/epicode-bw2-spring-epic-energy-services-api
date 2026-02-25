package it.epicode.bw2.epicenergyservices.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.epicode.bw2.epicenergyservices.dto.request.AddRuoloUtentiDTO;
import it.epicode.bw2.epicenergyservices.dto.response.ErrorsDTO;
import it.epicode.bw2.epicenergyservices.dto.response.ErrorsWithListDTO;
import it.epicode.bw2.epicenergyservices.entities.Utente;
import it.epicode.bw2.epicenergyservices.services.RuoliService;
import it.epicode.bw2.epicenergyservices.services.UtentiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/utenti")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Utenti", description = "Gestione utenti - Operazioni su utenti")
@SecurityRequirement(name = "Bearer Authentication")
public class UtentiController {

    private final RuoliService ruoliService;
    private final UtentiService utentiService;

    @PatchMapping("/ruoli")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Assegna ruolo a utente (ADMIN only)",
        description = "Assegna un ruolo specifico ad un utente esistente. Richiede ruolo ADMIN."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Ruolo assegnato con successo",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Utente.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Utente o Ruolo non trovato",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorsDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dati non validi",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorsDTO.class))
        ),
        @ApiResponse(
            responseCode = "422",
            description = "Errori di validazione",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorsWithListDTO.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Accesso negato - Solo ADMIN",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorsDTO.class))
        )
    })
    public Utente addRuoloUtente(
            @RequestBody @Valid AddRuoloUtentiDTO payload,
            Authentication auth
    ) {
        log.info("PATCH /utenti/ruoli - ADMIN '{}' sta assegnando ruolo ID {} a utente ID {}", 
                 auth.getName(), payload.idRuolo(), payload.idUtente());
        Utente updated = utentiService.addRuoloUtente(payload.idUtente(), payload.idRuolo());
        log.info("Ruolo assegnato con successo a utente ID {}", payload.idUtente());
        return updated;
    }
}
