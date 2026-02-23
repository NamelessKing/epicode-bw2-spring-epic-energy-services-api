package it.epicode.bw2.epicenergyservices.dto.response;

/**
 * DTO per risposta di login con JWT token
 * Ritornato da POST /auth/login dopo autenticazione riuscita
 * 
 * Esempio response:
 * {
 *   "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJFcGljRW5lcmd5IiwiZXhwIjoxNjkzODk5MjAwLCJpYXQiOjE2OTM4OTkxMDB9..."
 * }
 * 
 * Il token deve essere incluso negli header Authorization come:
 * Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
 */
public record LoginResponseDTO(
    String accessToken
) {}
