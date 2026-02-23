package it.epicode.bw2.epicenergyservices.entities;

/**
 * Enum che definisce i ruoli disponibili nel sistema
 * 
 * - USER: Accesso limitato (letture + inserimento clienti)
 * - ADMIN: Accesso completo (tutte le operazioni)
 * 
 * Estensibile in futuro per aggiungere ruoli dipartimentali
 */
public enum Role {
    USER,
    ADMIN
}
