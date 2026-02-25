package it.epicode.bw2.epicenergyservices.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.epicode.bw2.epicenergyservices.dto.request.ComuneRequestDTO;
import it.epicode.bw2.epicenergyservices.dto.response.ComuneResponseDTO;
import it.epicode.bw2.epicenergyservices.dto.response.ErrorsDTO;
import it.epicode.bw2.epicenergyservices.dto.response.ErrorsWithListDTO;
import it.epicode.bw2.epicenergyservices.services.ComuniService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comuni")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Comuni", description = "Gestione comuni italiani - Operazioni CRUD")
@SecurityRequirement(name = "Bearer Authentication")
public class ComuniController {

    private final ComuniService comuniService;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
            summary = "Lista paginata di tutti i comuni",
            description = "Recupera lista paginata di comuni italiani con i dettagli completi della provincia"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista recuperata con successo",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token JWT non valido o mancante",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorsDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Accesso negato - Ruolo non autorizzato",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorsDTO.class))
            )
    })
    public Page<ComuneResponseDTO> getAll(
            @Parameter(description = "Numero pagina (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Elementi per pagina (max 200)", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Campo per ordinamento", example = "nomeComune")
            @RequestParam(defaultValue = "nomeComune") String orderBy
    ) {
        log.debug("GET /comuni - page: {}, size: {}, orderBy: {}", page, size, orderBy);
        return comuniService.findAll(page, size, orderBy);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
            summary = "Trova comune per ID",
            description = "Recupera i dettagli completi di un comune specifico inclusi i dati della provincia"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Comune trovato",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ComuneResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Comune non trovato",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorsDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Non autenticato",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorsDTO.class))
            )
    })
    public ComuneResponseDTO getById(
            @Parameter(description = "ID del comune", example = "1", required = true)
            @PathVariable long id
    ) {
        log.debug("GET /comuni/{} richiesto", id);
        return comuniService.convertToResponseDTO(comuniService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Crea nuovo comune (ADMIN only)",
            description = "Crea un nuovo comune nel database. Richiede ruolo ADMIN. Il progressivo deve essere univoco."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Comune creato con successo",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ComuneResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dati non validi o comune già esistente",
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
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Provincia non trovata",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorsDTO.class))
            )
    })
    public ComuneResponseDTO create(
            @RequestBody @Valid ComuneRequestDTO payload,
            Authentication auth
    ) {
        log.info("POST /comuni - ADMIN '{}' sta creando comune: {}", auth.getName(), payload.nomeComune());
        ComuneResponseDTO created = comuniService.saveComune(payload);
        log.info("Comune creato: ID={}, Nome={}", created.id(), created.nomeComune());
        return created;
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Aggiorna comune (ADMIN only)",
            description = "Aggiorna i dati di un comune esistente. Richiede ruolo ADMIN."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Comune aggiornato con successo",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ComuneResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Comune o Provincia non trovati",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorsDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dati non validi o progressivo già in uso",
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
    public ComuneResponseDTO update(
            @Parameter(description = "ID del comune da aggiornare", example = "1", required = true)
            @PathVariable long id,
            @RequestBody @Valid ComuneRequestDTO payload,
            Authentication auth
    ) {
        log.info("PUT /comuni/{} - ADMIN '{}' sta aggiornando comune", id, auth.getName());
        ComuneResponseDTO updated = comuniService.findByIdAndUpdate(id, payload);
        log.info("Comune ID {} aggiornato con successo", id);
        return updated;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Elimina comune (ADMIN only)",
            description = "Elimina definitivamente un comune dal database. Richiede ruolo ADMIN.  ATTENZIONE: Operazione irreversibile!"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Comune eliminato con successo"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Comune non trovato",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorsDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Accesso negato - Solo ADMIN",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorsDTO.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Impossibile eliminare - Comune referenziato da altri record",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorsDTO.class))
            )
    })
    public void delete(
            @Parameter(description = "ID del comune da eliminare", example = "1", required = true)
            @PathVariable long id,
            Authentication auth
    ) {
        log.warn("DELETE /comuni/{} - ADMIN '{}' sta eliminando comune - IRREVERSIBILE!", id, auth.getName());
        comuniService.findByIdAndDelete(id);
        log.info("Comune ID {} eliminato definitivamente", id);
    }
}