package it.epicode.bw2.epicenergyservices.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import it.epicode.bw2.epicenergyservices.entities.Utente;
import it.epicode.bw2.epicenergyservices.exceptions.UnauthorizedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class JWTTools {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expirationms}")
    private long expirationMs;

    /**
     *Genera un JWT token per un utente con i RUOLI
     * Include il prefisso "ROLE_" per la compatibilità con @PreAuthorize
     */
    public String generateToken(Utente user) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());

        //Aggiungi il prefisso "ROLE_" ai nomi dei ruoli
        List<String> roleNames = user.getRuoliList().stream()
                .map(r -> "ROLE_" + r.getRuolo())
                .collect(Collectors.toList());

        return Jwts.builder()
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .subject(String.valueOf(user.getId()))
                .claim("roles", roleNames)
                .signWith(key)
                .compact();
    }

    /**
     *Estrai i ruoli dal JWT token (con il prefisso ROLE_)
     */
    public List<String> getRolesFromToken(String token) {
        try {
            Claims claims = extractClaims(token);
            Object roles = claims.get("roles");

            if (roles instanceof List) {
                return (List<String>) roles;
            }
            return List.of();
        } catch (Exception ex) {
            throw new UnauthorizedException("Impossibile estrarre ruoli dal token!");
        }
    }

    /**
     * Verifica che il token sia valido (firma + scadenza)
     */
    public void verifyToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());

            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);

        } catch (Exception ex) {
            throw new UnauthorizedException("Problemi col token! Effettua di nuovo il login!");
        }
    }

    /**
     * Estrae l'ID utente dal token
     */
    public Long extractIdFromToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());

            String subject = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();

            return Long.parseLong(subject);

        } catch (Exception ex) {
            throw new UnauthorizedException("Token non valido!");
        }
    }

    /**
     *Helper: Estrai i Claims dal token
     */
    private Claims extractClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}