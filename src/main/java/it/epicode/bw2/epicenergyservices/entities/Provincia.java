package it.epicode.bw2.epicenergyservices.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "La sigla della provincia è obbligatoria")
    private String sigla;

    @Column(nullable = false)
    @NotBlank(message = "La provincia è obbligatoria")
    private String provincia;

    @Column(nullable = false)
    @NotBlank(message = "La regione è obbligatoria")
    private String regione;

    @OneToMany(mappedBy = "provincia")
    private List<Comune> comuni = new ArrayList<>();

    public Provincia(String sigla, String provincia, String regione) {
        this.sigla = sigla;
        this.provincia = provincia;
        this.regione = regione;
    }
}