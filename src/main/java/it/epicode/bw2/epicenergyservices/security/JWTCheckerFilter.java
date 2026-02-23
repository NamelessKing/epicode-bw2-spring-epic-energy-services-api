package it.epicode.bw2.epicenergyservices.security;

import it.epicode.bw2.epicenergyservices.entities.User;
import it.epicode.bw2.epicenergyservices.exceptions.UnauthorizedException;
import it.epicode.bw2.epicenergyservices.services.UsersService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter che intercetta OGNI richiesta HTTP
 *
 * 1. Estrae il JWT token dall'header Authorization
 * 2. Verifica che il token sia valido
 * 3. Carica l'utente dal database
 * 4. Imposta l'utente nel SecurityContext per Spring Security
 * 
 * Viene eseguito UNA SOLA VOLTA per richiesta (OncePerRequestFilter)
 * 
 * Endpoints pubblici (nessun token richiesto):
 * - /auth/** (login, registrazione)
 * - /swagger-ui/** (API documentation)
 * - /v3/api-docs/** (OpenAPI spec)
 */
@Component
public class JWTCheckerFilter extends OncePerRequestFilter {
    
    private final JWTTools jwtTools;
    private final UsersService usersService;
    
    @Autowired
    public JWTCheckerFilter(JWTTools jwtTools, UsersService usersService) {
        this.jwtTools = jwtTools;
        this.usersService = usersService;
    }
    
    /**
     * Metodo principale del filter
     * Eseguito per OGNI richiesta HTTP (eccetto quelle pubbliche)
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain)
            throws ServletException, IOException {
        
        try {
            // 1. Estrai header Authorization
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new UnauthorizedException("Inserire il token nell'Authorization header con formato: Bearer <token>");
            }
            
            // 2. Estrai il token (rimuovi "Bearer " dal inizio)
            String token = authHeader.replace("Bearer ", "");
            
            // 3. Verifica che il token sia valido (firma + scadenza)
            jwtTools.verifyToken(token);
            
            // 4. Estrai ID utente dal token
            Long userId = jwtTools.extractIdFromToken(token);
            
            // 5. Carica utente dal database
            User authenticatedUser = usersService.findById(userId);
            
            // 6. Crea oggetto Authentication
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                authenticatedUser,
                null,
                authenticatedUser.getAuthorities()
            );
            
            // 7. Imposta Authentication nel SecurityContext
            // In questo modo Spring Security sa chi è l'utente autenticato
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
        } catch (UnauthorizedException ex) {
            // Log l'errore (per debugging)
            System.out.println("JWT Error: " + ex.getMessage());
            // La response sarà gestita dal global ErrorsHandler
        }
        
        // 8. Continua al prossimo filter nella catena
        filterChain.doFilter(request, response);
    }
    
    /**
     * Determina se questo filter deve essere saltato per una richiesta
     * 
     * @param request la richiesta HTTP
     * @return true se il filter deve essere saltato (endpoint pubblico)
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        AntPathMatcher matcher = new AntPathMatcher();
        String path = request.getServletPath();
        
        // Salta il filter per endpoint pubblici
        return matcher.match("/auth/**", path) ||
               matcher.match("/swagger-ui/**", path) ||
               matcher.match("/v3/api-docs/**", path) ||
               matcher.match("/swagger-ui.html", path);
    }
}
