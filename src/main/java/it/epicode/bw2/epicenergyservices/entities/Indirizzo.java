package it.epicode.bw2.epicenergyservices.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
    @Column(nullable = false)
    @NotBlank(message = "Via obbligatoria")
    private String via;

    @Column(nullable = false)
    @NotBlank(message = "Il civico è obbligatorio")
    private String civico;

    @Column(nullable = false)
    @NotBlank(message = "La località obbligatoria")
    private String localita;

    @Column(nullable = false)
    @NotBlank(message = "Il cap è obbligatorio")
    private String cap;

    @ManyToOne
    @JoinColumn(name = "id_comune", nullable = false)
    private Comune comune;


    public Indirizzo(String via,
                     String civico,
                     String localita,
                     String cap,
                     Comune comune
    ) {
        this.via = via;
        this.civico = civico;
        this.localita = localita;
        this.cap = cap;
        this.comune = comune;
    }
}
