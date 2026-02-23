package it.epicode.bw2.epicenergyservices.services;

import it.epicode.bw2.epicenergyservices.dto.request.LoginDTO;
import it.epicode.bw2.epicenergyservices.entities.User;
import it.epicode.bw2.epicenergyservices.exceptions.NotFoundException;
import it.epicode.bw2.epicenergyservices.exceptions.UnauthorizedException;
import it.epicode.bw2.epicenergyservices.security.JWTTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service per la gestione dell'autenticazione
 * - Verificare credenziali (email + password)
 * - Generare JWT token dopo autenticazione riuscita
 * - Gestire errori di autenticazione
 */
@Service
public class AuthService {
    
    private final UsersService usersService;
    private final JWTTools jwtTools;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public AuthService(UsersService usersService, 
                      JWTTools jwtTools, 
                      PasswordEncoder passwordEncoder) {
        this.usersService = usersService;
        this.jwtTools = jwtTools;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * Autentica un utente e genera JWT token
     * 1. Cerca utente per email nel database
     * 2. Verifica che la password corrisponda (usando BCrypt)
     * 3. Se credenziali valide → genera JWT token
     * 4. Se credenziali errate → lancia UnauthorizedException
     *
     * @throws NotFoundException se l'email non esiste
     * @throws UnauthorizedException se la password è sbagliata
     */
    public String checkCredentialsAndGenerateToken(LoginDTO payload) {
        // 1. Cerca user per email
        User found = usersService.findByEmail(payload.email());
        
        // 2. Verifica password con BCrypt
        // passwordEncoder.matches() confronta la password in chiaro con l'hash
        if (passwordEncoder.matches(payload.password(), found.getPasswordHash())) {
            // 3. Password corretta → genera JWT token
            String token = jwtTools.generateToken(found);
            System.out.println("Login riuscito per: " + found.getEmail() + " (ID: " + found.getId() + ")");
            return token;
        } else {
            // 4. Password sbagliata
            throw new UnauthorizedException("Credenziali errate!");
        }
    }
}
