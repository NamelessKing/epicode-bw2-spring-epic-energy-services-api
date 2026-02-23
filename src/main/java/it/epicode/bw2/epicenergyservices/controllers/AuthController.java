package it.epicode.bw2.epicenergyservices.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.epicode.bw2.epicenergyservices.dto.request.LoginDTO;
import it.epicode.bw2.epicenergyservices.dto.request.RegisterDTO;
import it.epicode.bw2.epicenergyservices.dto.response.ErrorsDTO;
import it.epicode.bw2.epicenergyservices.dto.response.ErrorsWithListDTO;
import it.epicode.bw2.epicenergyservices.dto.response.LoginResponseDTO;
import it.epicode.bw2.epicenergyservices.entities.Utente;
import it.epicode.bw2.epicenergyservices.exceptions.ValidationException;
import it.epicode.bw2.epicenergyservices.services.AuthService;
import it.epicode.bw2.epicenergyservices.services.UtentiService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints per autenticazione e registrazione")
public class AuthController {

    private final AuthService authService;
    private final UtentiService utentiService;

    @Autowired
    public AuthController(AuthService authService, UtentiService usersService) {
        this.authService = authService;
        this.utentiService = usersService;
    }

    @PostMapping("/login")
    @Operation(
            summary = "Login utente",
            description = "Autentica un utente con email e password, ritorna JWT token valido per 7 giorni"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Login riuscito, token ritornato",
                    content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Email non valida o password troppo corta",
                    content = @Content(schema = @Schema(implementation = ErrorsWithListDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Credenziali errate (email non esiste o password sbagliata)",
                    content = @Content(schema = @Schema(implementation = ErrorsDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Utente con questa email non trovato",
                    content = @Content(schema = @Schema(implementation = ErrorsDTO.class))
            )
    })
    public LoginResponseDTO login(@RequestBody @Valid LoginDTO payload) {
        String token = authService.checkCredentialsAndGenerateToken(payload);
        return new LoginResponseDTO(token);
    }


    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Registrazione utente",
            description = "Registra un nuovo utente nel sistema con email e password"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Utente registrato con successo",
                    content = @Content(schema = @Schema(implementation = Utente.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dati invalidi o duplicati",
                    content = @Content(schema = @Schema(implementation = ErrorsDTO.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Errori di validazione dettagliati",
                    content = @Content(schema = @Schema(implementation = ErrorsWithListDTO.class))
            )
    })


    public Utente register(@RequestBody @Valid RegisterDTO payload,
                           BindingResult validationResult) {
        // Gestisci errori di validazione
        if (validationResult.hasErrors()) {
            List<String> errors = validationResult.getFieldErrors()
                    .stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());
            throw new ValidationException("Errori di validazione", errors);
        }

        // Se validazione OK, registra l'utente
        Utente saved = this.utentiService.addUtente(payload);
        return saved;
    }
}
