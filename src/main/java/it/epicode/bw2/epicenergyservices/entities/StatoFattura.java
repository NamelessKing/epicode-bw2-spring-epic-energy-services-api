package it.epicode.bw2.epicenergyservices.entities;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "stato_fattura")
public class StatoFattura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String stato;

    public StatoFattura() {
    }

    ;

    public StatoFattura(String stato) {
        this.stato = stato;
    }

    public long getId() {
        return id;
    }


    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }
}
