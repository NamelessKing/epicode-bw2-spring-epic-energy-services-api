package it.epicode.bw2.epicenergyservices.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sede cliente")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class SedeCliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private long id;
    @Enumerated(EnumType.STRING)
    private TipoSede tipoSede;
    private Long idCliente;


//    @OneToMany
//    @JoinColumn(name = "id_cliente", nullable = false)
//    private Clienti clienti;

    @ManyToOne
    @JoinColumn(name = "id_indirizzo", nullable = false)
    private Indirizzo indirizzo;

    public SedeCliente(TipoSede tipoSede, Long idCliente, Indirizzo indirizzo) {
        this.tipoSede = tipoSede;
        this.idCliente = idCliente;
        this.indirizzo = indirizzo;
    }
}
