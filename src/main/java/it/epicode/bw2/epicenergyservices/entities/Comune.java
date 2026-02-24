package it.epicode.bw2.epicenergyservices.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "comuni")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "provincia")
public class Comune {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private long id;

    @Column(name = "progressivo_del_comune", nullable = false)
    private int progressivoDelComune;

    @Column(name = "nome_comune", nullable = false)
    private String nomeComune;

    @ManyToOne
    @JoinColumn(name = "id_provincia", nullable = false)
    private Provincia provincia;

    public Comune(int progressivoDelComune, String nomeComune, Provincia provincia) {
        this.progressivoDelComune = progressivoDelComune;
        this.nomeComune = nomeComune;
        this.provincia = provincia;
    }
}