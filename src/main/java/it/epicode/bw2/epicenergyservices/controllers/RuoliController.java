package it.epicode.bw2.epicenergyservices.controllers;

import it.epicode.bw2.epicenergyservices.dto.request.RuoliDTO;
import it.epicode.bw2.epicenergyservices.entities.Ruolo;
import it.epicode.bw2.epicenergyservices.services.RuoliService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Solo ADMIN può accedere a questi endpoint
 */
@RestController
@RequestMapping("/api/ruoli")
@Tag(name = "Ruoli", description = "Gestione ruoli utenti (ADMIN only)")
@SecurityRequirement(name = "Bearer Authentication")
public class RuoliController {

    private final RuoliService ruoliService;

    @Autowired
    public RuoliController(RuoliService ruoliService) {
        this.ruoliService = ruoliService;
    }


    /**
     * Crea un nuovo ruolo (ADMIN only)
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crea un nuovo ruolo (ADMIN only)")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Ruolo creato con successo"),
        @ApiResponse(responseCode = "400", description = "Dati non validi"),
        @ApiResponse(responseCode = "403", description = "Accesso negato (non ADMIN)")
    })
    public ResponseEntity<RuoliDTO> addRuolo(@Valid @RequestBody RuoliDTO payload) {
        Ruolo createdRuolo = ruoliService.addRuolo(payload);
        RuoliDTO dto = toRuoliDTO(createdRuolo);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    
    /**
     * Converte Ruolo entity a RuoliDTO per la response
     */
    private RuoliDTO toRuoliDTO(Ruolo ruolo) {
        return new RuoliDTO(ruolo.getRuolo());
    }
}