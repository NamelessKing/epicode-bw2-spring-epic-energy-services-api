package it.epicode.bw2.epicenergyservices.controllers;

import it.epicode.bw2.epicenergyservices.dto.request.LoginDTO;
import it.epicode.bw2.epicenergyservices.dto.request.RegisterDTO;
import it.epicode.bw2.epicenergyservices.dto.response.LoginResponseDTO;
import it.epicode.bw2.epicenergyservices.dto.response.UserResponseDTO;
import it.epicode.bw2.epicenergyservices.entities.User;
import it.epicode.bw2.epicenergyservices.exceptions.ValidationException;
import it.epicode.bw2.epicenergyservices.services.AuthService;
import it.epicode.bw2.epicenergyservices.services.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller per la gestione dell'autenticazione
 * 
 * Endpoints:
 * - POST /auth/login - Login utente (genera JWT token)
 * - POST /auth/register - Registrazione nuovo utente
 * 
 * Nessuno di questi endpoint richiede autenticazione (sono pubblici)
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints per autenticazione e registrazione")
public class AuthController {
    
    private final AuthService authService;
    private final UsersService usersService;
    
    @Autowired
    public AuthController(AuthService authService, UsersService usersService) {
        this.authService = authService;
        this.usersService = usersService;
    }
    
    /**
     * Endpoint di login
     * Autentica un utente e ritorna JWT token
     * 
     * @param payload LoginDTO con email e password
     * @return LoginResponseDTO con accessToken (JWT)
     * 
     * Esempio request:
     * POST /auth/login
     * {
     *   "email": "admin@epicenergy.it",
     *   "password": "admin123"
     * }
     * 
     * Esempio response (200 OK):
     * {
     *   "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
     * }
     * 
     * Client deve aggiungere il token negli header successivi:
     * Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
     */
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
            description = "Email non valida o password troppo corta"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Credenziali errate (email non esiste o password sbagliata)"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Utente con questa email non trovato"
        )
    })
    public LoginResponseDTO login(@RequestBody @Valid LoginDTO payload) {
        String token = authService.checkCredentialsAndGenerateToken(payload);
        return new LoginResponseDTO(token);
    }
    
    /**
     * Endpoint di registrazione
     * Registra un nuovo utente nel sistema
     * 
     * @param payload RegisterDTO con dati di registrazione
     * @param validationResult risultato della validazione Bean Validation
     * @return User appena registrato (senza password!)
     * 
     * Esempio request:
     * POST /auth/register
     * {
     *   "username": "mario_rossi",
     *   "email": "mario@epicenergy.it",
     *   "password": "securePass123",
     *   "role": "USER",
     *   "firstName": "Mario",
     *   "lastName": "Rossi",
     *   "avatarUrl": "https://api.example.com/avatars/mario.jpg"
     * }
     * 
     * Esempio response (201 CREATED):
     * {
     *   "id": 2,
     *   "username": "mario_rossi",
     *   "email": "mario@epicenergy.it",
     *   "firstName": "Mario",
     *   "lastName": "Rossi",
     *   "role": "USER",
     *   "avatarUrl": "https://api.example.com/avatars/mario.jpg"
     * }
     * 
     * Validazioni:
     * - username: obbligatorio, 3-50 caratteri
     * - email: obbligatoria, formato valido, non già in uso
     * - password: obbligatoria, almeno 6 caratteri
     * - role: obbligatorio, USER o ADMIN
     * - firstName, lastName, avatarUrl: opzionali
     */
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
            content = @Content(schema = @Schema(implementation = UserResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dati invalidi, email/username già in uso, o errori di validazione"
        ),
        @ApiResponse(
            responseCode = "422",
            description = "Errori di validazione dettagliati"
        )
    })
    public UserResponseDTO register(@RequestBody @Valid RegisterDTO payload,
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
        User saved = usersService.save(payload);
        return new UserResponseDTO(
            saved.getId(),
            saved.getUsername(),
            saved.getEmail(),
            saved.getFirstName(),
            saved.getLastName(),
            saved.getAvatarUrl(),
            saved.getRole()
        );
    }
}