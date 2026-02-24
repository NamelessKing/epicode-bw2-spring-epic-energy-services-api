package it.epicode.bw2.epicenergyservices.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "stato_fattura")
public class StatoFattura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Lo stato fattura è obbligatorio")
    private String stato;

    public StatoFattura() {
    }


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
