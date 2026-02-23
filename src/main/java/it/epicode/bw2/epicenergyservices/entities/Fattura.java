package it.epicode.bw2.epicenergyservices.entities;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "fattura")
public class Fattura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private LocalDate data;
    private double importo;
    private long numero;//secondo me può essere l'id

    //relazione ManyToOne con stato fattura
    @ManyToOne
    @JoinColumn(name = "id_stato_fattura")
    private StatoFattura stato;

    //Relazione OneToMany con id_cliente

    //relazione ManyToOne con id_utente
//    @ManyToOne
//    @JoinColumn(name="id_utente")
//    private Utente utente;

    public Fattura() {
    }

    ;

    public Fattura(LocalDate data, double importo, long numero, StatoFattura stato) {
        this.data = data;
        this.importo = importo;
        this.numero = numero;
        this.stato = stato;
    }

    public long getId() {
        return id;
    }


    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public double getImporto() {
        return importo;
    }

    public void setImporto(double importo) {
        this.importo = importo;
    }

    public long getNumero() {
        return numero;
    }

    public void setNumero(long numero) {
        this.numero = numero;
    }

    public StatoFattura getStato() {
        return stato;
    }

    public void setStato(StatoFattura stato) {
        this.stato = stato;
    }
}
