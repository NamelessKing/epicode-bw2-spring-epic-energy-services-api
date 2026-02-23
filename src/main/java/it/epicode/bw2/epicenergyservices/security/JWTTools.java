package it.epicode.bw2.epicenergyservices.security;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import it.epicode.bw2.epicenergyservices.entities.Utente;
import it.epicode.bw2.epicenergyservices.exceptions.UnauthorizedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * - Generare token JWT durante il login
 * - Verificare validità del token (firma e scadenza)
 * - Estrarre ID utente dal token
 */
@Component
public class JWTTools {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expirationms}")
    private long expirationMs;

    /**
     * Genera un JWT token per un utente
     * Esempio token generato:
     * eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJFcGljRW5lcmd5IiwiZXhwIjoxNjkzODk5MjAwLCJpYXQiOjE2OTM4OTkxMDB9...
     */
    public String generateToken(Utente user) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());

        return Jwts.builder()
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .subject(String.valueOf(user.getId())) // Memorizza ID utente nel token
                .signWith(key)
                .compact();
    }

    /**
     * Verifica che il token sia valido
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
}
