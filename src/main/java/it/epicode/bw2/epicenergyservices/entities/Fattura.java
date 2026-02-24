package it.epicode.bw2.epicenergyservices.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "fattura")
public class Fattura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private LocalDate data;
    private double importo;
    private long numero;//secondo me può essere l'id

    //Relazione ManyToOne con id_cliente
    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    //relazione ManyToOne con id_stato_fattura
    @ManyToOne
    @JoinColumn(name = "id_stato_fattura")
    private StatoFattura statoFattura;


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
        this.statoFattura = stato;
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

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<StatoFattura> getStatoFattura() {
        return statoFattura;
    }

    public void setStatoFattura(List<StatoFattura> statoFattura) {
        this.statoFattura = statoFattura;
    }
}
