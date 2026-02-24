package it.epicode.bw2.epicenergyservices.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "fattura")
public class Fattura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false)
    @PositiveOrZero
    private double importo;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Il numero della fattura è obbligatorio")
    private long numero;

    //Relazione ManyToOne con id_cliente
    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    //relazione ManyToOne con id_stato_fattura
    @ManyToOne
    @JoinColumn(name = "id_stato_fattura", nullable = false)
    private StatoFattura statoFattura;


    //relazione ManyToOne con id_utente
    @ManyToOne
    @JoinColumn(name = "id_utente", nullable = false)
    private Utente utente;

    public Fattura() {
    }


    public Fattura(LocalDate data, double importo, long numero, StatoFattura statoFattura) {
        this.data = data;
        this.importo = importo;
        this.numero = numero;
        this.statoFattura = statoFattura;
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
        return statoFattura;
    }

    public void setStato(StatoFattura statoFattura) {
        this.statoFattura = statoFattura;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Utente getUtente() {
        return utente;
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
    }
}