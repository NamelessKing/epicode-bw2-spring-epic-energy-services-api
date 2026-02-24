package it.epicode.bw2.epicenergyservices.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "province")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "comuni")
@JsonIgnoreProperties({"comuni"})
public class Provincia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private long id;

    @Column(nullable = false, unique = true, length = 2)
    private String sigla;

    @Column(nullable = false)
    private String provincia;

    @Column(nullable = false)
    private String regione;

    @OneToMany(mappedBy = "provincia")
    private List<Comune> comuni = new ArrayList<>();

    public Provincia(String sigla, String provincia, String regione) {
        this.sigla = sigla;
        this.provincia = provincia;
        this.regione = regione;
    }
}