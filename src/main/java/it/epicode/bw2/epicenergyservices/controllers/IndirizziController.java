package it.epicode.bw2.epicenergyservices.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.epicode.bw2.epicenergyservices.dto.request.IndirizzoRequestDTO;
import it.epicode.bw2.epicenergyservices.dto.response.ErrorsDTO;
import it.epicode.bw2.epicenergyservices.dto.response.ErrorsWithListDTO;
import it.epicode.bw2.epicenergyservices.dto.response.IndirizzoResponseDTO;
import it.epicode.bw2.epicenergyservices.services.IndirizziService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/indirizzi")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Indirizzi", description = "Gestione indirizzi italiani - Operazioni CRUD")
@SecurityRequirement(name = "Bearer Authentication")
public class IndirizziController {

    private final IndirizziService indirizziService;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
            summary = "Lista paginata di tutti gli indirizzi",
            description = "Recupera lista paginata di indirizzi con dettagli completi del comune associato"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista recuperata con successo",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token JWT non valido o mancante",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorsDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Accesso negato - Ruolo non autorizzato",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorsDTO.class))
            )
    })
    public Page<IndirizzoResponseDTO> getAll(
            @Parameter(description = "Numero pagina (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Elementi per pagina (max 200)", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Campo per ordinamento", example = "via")
            @RequestParam(defaultValue = "via") String orderBy
    ) {
        log.debug("GET /indirizzi - page: {}, size: {}, orderBy: {}", page, size, orderBy);
        return indirizziService.findAll(page, size, orderBy);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
            summary = "Trova indirizzo per ID",
            description = "Recupera i dettagli completi di un indirizzo specifico incluso il comune associato"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Indirizzo trovato",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = IndirizzoResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Indirizzo non trovato",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorsDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Non autenticato",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorsDTO.class))
            )
    })
    public IndirizzoResponseDTO getById(
            @Parameter(description = "ID dell'indirizzo", example = "1", required = true)
            @PathVariable long id
    ) {
        log.debug("GET /indirizzi/{} richiesto", id);
        return indirizziService.findById(id);
    }

//    @PostMapping TODO QUESTO è STATO COMENTATO APPOSTA PERCHè MOLTO PROBABILMENTE POTREBBE NON SERVIRE
//    @ResponseStatus(HttpStatus.CREATED)
//    @PreAuthorize("hasAnyRole('ADMIN','USER')")
//    @Operation(
//            summary = "Crea nuovo indirizzo (ADMIN only)",
//            description = "Crea un nuovo indirizzo nel database. Richiede ruolo ADMIN. L'indirizzo deve essere univoco nel comune."
//    )
//    @ApiResponses({
//            @ApiResponse(
//                    responseCode = "201",
//                    description = "Indirizzo creato con successo",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = IndirizzoResponseDTO.class))
//            ),
//            @ApiResponse(
//                    responseCode = "400",
//                    description = "Dati non validi o indirizzo già esistente",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = ErrorsDTO.class))
//            ),
//            @ApiResponse(
//                    responseCode = "422",
//                    description = "Errori di validazione",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = ErrorsWithListDTO.class))
//            ),
//            @ApiResponse(
//                    responseCode = "403",
//                    description = "Accesso negato - Solo ADMIN",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = ErrorsDTO.class))
//            ),
//            @ApiResponse(
//                    responseCode = "404",
//                    description = "Comune non trovato",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = ErrorsDTO.class))
//            )
//    })
//    public IndirizzoResponseDTO create(
//            @RequestBody @Valid IndirizzoRequestDTO payload,
//            Authentication auth
//    ) {
//        log.info("POST /indirizzi - ADMIN '{}' sta creando indirizzo in via {}",
//                auth.getName(), payload.via());
//
//        IndirizzoResponseDTO created = indirizziService.save(payload);
//
//        log.info("Indirizzo creato: ID={}, Via={}", created.id(), created.via());
//        return created;
//    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(
            summary = "Aggiorna indirizzo (ADMIN only)",
            description = "Aggiorna i dati di un indirizzo esistente. Richiede ruolo ADMIN."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Indirizzo aggiornato con successo",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = IndirizzoResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Indirizzo o Comune non trovati",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorsDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dati non validi",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorsDTO.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Errori di validazione",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorsWithListDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Accesso negato - Solo ADMIN",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorsDTO.class))
            )
    })
    public IndirizzoResponseDTO update(
            @Parameter(description = "ID dell'indirizzo da aggiornare", example = "1", required = true)
            @PathVariable long id,
            @RequestBody @Valid IndirizzoRequestDTO payload,
            Authentication auth
    ) {
        log.info("PUT /indirizzi/{} - ADMIN '{}' sta aggiornando indirizzo",
                id, auth.getName());

        IndirizzoResponseDTO updated = indirizziService.findByIdAndUpdate(id, payload);

        log.info("Indirizzo ID {} aggiornato con successo", id);
        return updated;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(
            summary = "Elimina indirizzo (ADMIN only)",
            description = "Elimina definitivamente un indirizzo dal database. Operazione irreversibile."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Indirizzo eliminato con successo"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Indirizzo non trovato",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorsDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Accesso negato - Solo ADMIN",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorsDTO.class))
            )
    })
    public void delete(
            @Parameter(description = "ID dell'indirizzo da eliminare", example = "1", required = true)
            @PathVariable long id,
            Authentication auth
    ) {
        log.warn("DELETE /indirizzi/{} - ADMIN '{}' sta eliminando indirizzo - IRREVERSIBILE!",
                id, auth.getName());

        indirizziService.findByIdAndDelete(id);

        log.info("Indirizzo ID {} eliminato definitivamente", id);
    }
//    {
//      "via": "roma",
//      "civico": "1",
//      "localita": "localitAA",
//      "cap": "10110",
//      "comuneId": 1
//    }

}