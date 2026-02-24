package it.epicode.bw2.epicenergyservices.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "indirizzi")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Indirizzo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private long id;
    private String via;
    private Long civico;
    private String localita;
    private Long cap;

//    @ManyToOne
//    @JoinColumn(name = "id_comune", nullable = false)
//    private Comune comune;

    @OneToMany(mappedBy = "indirizzo")
    private List<SedeCliente> sediCliente = new ArrayList<>();


    public Indirizzo(String via, Long civico, String localita, Long cap, List<SedeCliente> sediCliente) {
        this.via = via;
        this.civico = civico;
        this.localita = localita;
        this.cap = cap;
//        this.comune = comune;
        this.sediCliente = sediCliente;
    }
}
