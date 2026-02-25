package it.epicode.bw2.epicenergyservices.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.epicode.bw2.epicenergyservices.dto.request.ProvinciaRequestDTO;
import it.epicode.bw2.epicenergyservices.dto.response.ErrorsDTO;
import it.epicode.bw2.epicenergyservices.dto.response.ErrorsWithListDTO;
import it.epicode.bw2.epicenergyservices.dto.response.ProvinciaResponseDTO;
import it.epicode.bw2.epicenergyservices.services.ProvinceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/province")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Province", description = "Gestione province italiane - Operazioni CRUD")
@SecurityRequirement(name = "Bearer Authentication")
public class ProvinceController {

    private final ProvinceService provinceService;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
            summary = "Lista paginata di tutte le province",
            description = "Recupera lista paginata di province italiane con dettagli completi"
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
    public Page<ProvinciaResponseDTO> getAll(
            @Parameter(description = "Numero pagina (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Elementi per pagina (max 200)", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Campo per ordinamento", example = "provincia")
            @RequestParam(defaultValue = "provincia") String orderBy
    ) {
        log.debug("GET /province - page: {}, size: {}, orderBy: {}", page, size, orderBy);
        return provinceService.findAll(page, size, orderBy);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
            summary = "Trova provincia per ID",
            description = "Recupera i dettagli completi di una provincia specifica"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Provincia trovata",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProvinciaResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Provincia non trovata",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorsDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Non autenticato",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorsDTO.class))
            )
    })
    public ProvinciaResponseDTO getById(
            @Parameter(description = "ID della provincia", example = "58", required = true)
            @PathVariable long id
    ) {
        log.debug("GET /province/{} richiesto", id);
        return provinceService.convertToResponseDTO(provinceService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Crea nuova provincia (ADMIN only)",
            description = "Crea una nuova provincia nel database. Richiede ruolo ADMIN. La sigla deve essere univoca."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Provincia creata con successo",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProvinciaResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dati non validi o provincia già esistente",
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
    public ProvinciaResponseDTO create(
            @RequestBody @Valid ProvinciaRequestDTO payload,
            Authentication auth
    ) {
        log.info("POST /province - ADMIN '{}' sta creando provincia: {} ({})",
                auth.getName(), payload.provincia(), payload.sigla());
        ProvinciaResponseDTO created = provinceService.saveProvincia(payload);
        log.info("Provincia creata: ID={}, Sigla={}", created.id(), created.sigla());
        return created;
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Aggiorna provincia (ADMIN only)",
            description = "Aggiorna i dati di una provincia esistente. Richiede ruolo ADMIN."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Provincia aggiornata con successo",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProvinciaResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Provincia non trovata",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorsDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dati non validi o sigla già in uso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorsDTO.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Errori di validazione",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorsDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Accesso negato - Solo ADMIN",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorsDTO.class))
            )
    })
    public ProvinciaResponseDTO update(
            @Parameter(description = "ID della provincia da aggiornare", example = "58", required = true)
            @PathVariable long id,
            @RequestBody @Valid ProvinciaRequestDTO payload,
            Authentication auth
    ) {
        log.info("PUT /province/{} - ADMIN '{}' sta aggiornando provincia", id, auth.getName());
        ProvinciaResponseDTO updated = provinceService.findByIdAndUpdate(id, payload);
        log.info("Provincia ID {} aggiornata con successo", id);
        return updated;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Elimina provincia (ADMIN only)",
            description = "Elimina definitivamente una provincia dal database. Richiede ruolo ADMIN. ⚠️ ATTENZIONE: Operazione irreversibile!"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Provincia eliminata con successo"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Provincia non trovata",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorsDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Accesso negato - Solo ADMIN",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorsDTO.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Impossibile eliminare - Provincia referenziata da comuni",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorsDTO.class))
            )
    })
    public void delete(
            @Parameter(description = "ID della provincia da eliminare", example = "58", required = true)
            @PathVariable long id,
            Authentication auth
    ) {
        log.warn("DELETE /province/{} - ⚠️ ADMIN '{}' sta eliminando provincia - IRREVERSIBILE!",
                id, auth.getName());
        provinceService.findByIdAndDelete(id);
        log.info("Provincia ID {} eliminata definitivamente", id);
    }
}
