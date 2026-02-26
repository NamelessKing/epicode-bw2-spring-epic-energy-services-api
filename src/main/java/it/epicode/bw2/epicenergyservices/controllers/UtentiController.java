package it.epicode.bw2.epicenergyservices.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.epicode.bw2.epicenergyservices.dto.request.RegisterDTO;
import it.epicode.bw2.epicenergyservices.dto.request.RuoloUtentiDTO;
import it.epicode.bw2.epicenergyservices.dto.request.UpdateUtentiAdminDTO;
import it.epicode.bw2.epicenergyservices.dto.request.UpdateUtentiUserDTO;
import it.epicode.bw2.epicenergyservices.dto.response.ErrorsDTO;
import it.epicode.bw2.epicenergyservices.dto.response.ErrorsWithListDTO;
import it.epicode.bw2.epicenergyservices.entities.Utente;
import it.epicode.bw2.epicenergyservices.exceptions.ValidationException;
import it.epicode.bw2.epicenergyservices.services.RuoliService;
import it.epicode.bw2.epicenergyservices.services.UtentiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/utenti")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Utenti", description = "Gestione utenti - Operazioni su utenti")
@SecurityRequirement(name = "Bearer Authentication")
public class UtentiController {

    private final RuoliService ruoliService;
    private final UtentiService utentiService;


    //ASSEGNA RUOLO
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
    public Utente addRuoloUtente(@RequestBody @Valid RuoloUtentiDTO payload,
                                 Authentication auth
    ) {
        log.info("PATCH /utenti/ruoli - ADMIN '{}' sta assegnando ruolo ID {} a utente ID {}",
                auth.getName(), payload.idRuolo(), payload.idUtente());
        Utente updated = utentiService.addRuoloUtente(payload.idUtente(), payload.idRuolo());
        log.info("Ruolo assegnato con successo a utente ID {}", payload.idUtente());
        return updated;
    }


    //Cancella RUOLO UTENTE

    @DeleteMapping("/ruoli")
    @Operation(summary = "Cancella ruolo da utente")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteRole(@RequestBody RuoloUtentiDTO payload) {
        this.utentiService.removeRuoloUtente(payload.idUtente(), payload.idRuolo());
    }


    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Visualizza tutti gli utenti (ADMIN only)")
                                     @RequestParam(defaultValue = "5") int size,
                                     @RequestParam(defaultValue = "username") String orderBy) {
        return this.utentiService.findAll(page, size, orderBy);
    }

    ;

    @GetMapping("/{utenteId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Visualizza un utente (ADMIN only)")
    public Utente getUtente(@PathVariable Long utenteId) {
        return this.utentiService.findById(utenteId);
    }

    ;

    //visualizza proprio profilo ---------
    @GetMapping("/me")
    @Operation(summary = "Visualizza proprio profilo")
    public Utente getMyProfile(@AuthenticationPrincipal Utente utenteCorrente) {
        return this.utentiService.findById(utenteCorrente.getId());
    }

    // ADD UTENTE---------------------
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Aggiunge nuovo utente (ADMIN only)")
    public Utente addUser(@RequestBody @Valid RegisterDTO payload,
                          BindingResult validationResult) {
        // Gestisci errori di validazione
        if (validationResult.hasErrors()) {
            List<String> errors = validationResult.getFieldErrors()
                    .stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());
            throw new ValidationException(errors);
        }

        // Se validazione OK, registra l'utente
        Utente saved = this.utentiService.addUtente(payload, "USER");
        return saved;
    }


    //update profilo da user
    @PatchMapping("/me/update")
    @Operation(summary = "Modifica proprio proilo")
    public Utente updateMyProfile(@AuthenticationPrincipal Utente utenteCorrente, @RequestBody @Validated UpdateUtentiUserDTO payload) {
        return utentiService.findByIdAndUpdateByUser(payload, utenteCorrente.getId());
    }

    //update profilo da admin
    @PutMapping("/{utenteId}/update")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Modifica un utente (ADMIN only)")
    public Utente updateUtenteProfile(@PathVariable Long utenteId, @RequestBody @Validated UpdateUtentiAdminDTO payload) {
        return utentiService.findByIdAndUpdateByAdmin(payload, utenteId);
    }

    //cambia avatar
    @PatchMapping("/me/avatar")
    @Operation(summary = "Modifica avatar")
    public Utente uploadImage(@RequestParam("avatar_pic") MultipartFile file, @AuthenticationPrincipal Utente utenteCorrente) {

        return this.utentiService.uploadAvatar(file, utenteCorrente.getId());
    }


}
