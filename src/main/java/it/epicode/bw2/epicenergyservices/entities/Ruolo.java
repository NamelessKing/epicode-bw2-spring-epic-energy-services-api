package it.epicode.bw2.epicenergyservices.entities;

import jakarta.persistence.*;

/**
 * Enum che definisce i ruoli disponibili nel sistema
 * <p>
 * - USER: Accesso limitato (letture + inserimento clienti)
 * - ADMIN: Accesso completo (tutte le operazioni)
 * <p>
 * Estensibile in futuro per aggiungere ruoli dipartimentali
 */
@Entity
@Table(name = "ruoli")
public class Ruolo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String ruolo;


    public Ruolo(String ruolo) {
        this.ruolo = ruolo;
    }

    public Ruolo() {

    }

    public long getId() {
        return id;
    }

    public String getRuolo() {
        return ruolo;
    }

    public void setRuolo(String ruolo) {
        this.ruolo = ruolo;
    }

    @Override
    public String toString() {
        return "Ruolo{" +
                "id=" + id +
                ", ruolo='" + ruolo + '\'' +
                '}';
    }
}
