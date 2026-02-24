package it.epicode.bw2.epicenergyservices.entities;

import jakarta.persistence.*;
import lombok.*;

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
    private String civico;
    private String localita;
    private String cap;

//    @ManyToOne
//    @JoinColumn(name = "id_comune", nullable = false)
//    private Comune comune;


    public Indirizzo(String via, String civico, String localita, String cap
//            , Comune comune
    ) {
        this.via = via;
        this.civico = civico;
        this.localita = localita;
        this.cap = cap;
        // this.comune = comune;
    }
}
