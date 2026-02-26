package it.epicode.bw2.epicenergyservices.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Entity che rappresenta un cliente business dell'azienda.
 * I clienti possono avere fino a due indirizzi: sede legale (obbligatoria) e sede operativa (opzionale).
 */
@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ==================== SOFT DELETE ====================
    /**
     * Flag per soft delete. Se true, il cliente è stato eliminato logicamente.
     * I clienti cancellati non vengono mostrati nelle liste ma rimangono nel DB.
     */
    @Setter
    @Column(name = "cancellato", nullable = false)
    private Boolean cancellato = false;

    // ==================== DATI AZIENDA ====================

    @Setter
    @Column(nullable = false, length = 255)
    @NotBlank(message = "La ragione sociale è obbligatoria")
    @Size(min = 2, max = 255, message = "La ragione sociale deve essere tra 2 e 255 caratteri")
    private String ragioneSociale;

    @Setter
    @Column(nullable = false, unique = true, length = 11)
    @NotBlank(message = "La partita IVA è obbligatoria")
    @Pattern(regexp = "^[0-9]{11}$", message = "La partita IVA deve essere di 11 cifre numeriche")
    private String partitaIva;

    @Setter
    @Column(nullable = false, unique = true)
    @NotBlank(message = "L'email del cliente è obbligatoria")
    @Email(message = "Formato email non valido")
    private String email;

    @Setter
    @Column(nullable = false)
    @NotNull(message = "La data di inserimento è obbligatoria")
    private LocalDate dataInserimento;

    @Setter
    @Column(nullable = true)
    private LocalDate dataUltimoContatto;

    @Setter
    @Column(nullable = false)
    @NotNull(message = "Il fatturato annuale è obbligatorio")
    @PositiveOrZero(message = "Il fatturato annuale deve essere maggiore o uguale a zero")
    private Double fatturatoAnnuale;

    @Setter
    @Column(nullable = false, unique = true)
    @NotBlank(message = "La PEC è obbligatoria")
    @Email(message = "Formato PEC non valido")
    private String pec;

    @Setter
    @Column(length = 20)
    @Pattern(regexp = "^[0-9 +()-]{6,20}$", message = "Il numero di telefono non è valido")
    private String telefono;

    @Setter
    @Column(length = 500)
    private String logoAziendale;

    @Setter
    @Column(nullable = false, length = 10)
    @NotNull(message = "Il tipo di azienda è obbligatorio")
    @Enumerated(EnumType.STRING)
    private TipoAzienda tipo;

    // ==================== DATI CONTATTO ====================

    @Setter
    @Column(nullable = false)
    @NotBlank(message = "L'email del contatto è obbligatoria")
    @Email(message = "Formato email contatto non valido")
    private String emailContatto;

    @Setter
    @Column(nullable = false, length = 50)
    @NotBlank(message = "Il nome del contatto è obbligatorio")
    @Size(min = 2, max = 50, message = "Il nome del contatto deve essere tra 2 e 50 caratteri")
    private String nomeContatto;

    @Setter
    @Column(nullable = false, length = 50)
    @NotBlank(message = "Il cognome del contatto è obbligatorio")
    @Size(min = 2, max = 50, message = "Il cognome del contatto deve essere tra 2 e 50 caratteri")
    private String cognomeContatto;

    @Setter
    @Column(nullable = false, length = 20)
    @NotBlank(message = "Il telefono del contatto è obbligatorio")
    @Pattern(regexp = "^[0-9 +()-]{6,20}$", message = "Il numero di telefono del contatto non è valido")
    private String telefonoContatto;

    // ==================== RELAZIONI ====================

    /**
     * Sede legale del cliente (OBBLIGATORIA)
     */
    @Setter
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_sede_legale", nullable = false)
    @NotNull(message = "La sede legale è obbligatoria")
    private Indirizzo indirizzoSedeLegale;

    /**
     * Sede operativa del cliente (OPZIONALE)
     */
    @Setter
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_sede_operativa", nullable = true)
    private Indirizzo indirizzoSedeOperativa;

    // ==================== COSTRUTTORI ====================

    public Cliente() {
    }

    /**
     * Costruttore completo per la creazione di un nuovo cliente
     */
    public Cliente(
            String ragioneSociale,
            String partitaIva,
            String email,
            Double fatturatoAnnuale,
            String pec,
            String telefono,
            String logoAziendale,
            TipoAzienda tipo,
            String emailContatto,
            String nomeContatto,
            String cognomeContatto,
            String telefonoContatto,
            Indirizzo sedeLegale,
            Indirizzo sedeOperativa
    ) {
        this.ragioneSociale = ragioneSociale;
        this.partitaIva = partitaIva;
        this.email = email;
        this.dataInserimento = LocalDate.now(); // Auto-generato alla creazione
        this.dataUltimoContatto = null; // Inizialmente null, verrà aggiornato al primo contatto
        this.fatturatoAnnuale = fatturatoAnnuale;
        this.pec = pec;
        this.telefono = telefono;
        this.logoAziendale = logoAziendale;
        this.tipo = tipo;
        this.emailContatto = emailContatto;
        this.nomeContatto = nomeContatto;
        this.cognomeContatto = cognomeContatto;
        this.telefonoContatto = telefonoContatto;
        this.indirizzoSedeLegale = sedeLegale;
        this.indirizzoSedeOperativa = sedeOperativa;
        this.cancellato = false; // Di default il cliente è attivo
    }

    // ==================== GETTERS E SETTERS ====================

    public Long getId() {
        return id;
    }

    public Boolean getCancellato() {
        return cancellato;
    }

    /**
     * Helper method per verificare se il cliente è attivo (non cancellato)
     */
    public boolean isAttivo() {
        return !cancellato;
    }

    public String getRagioneSociale() {
        return ragioneSociale;
    }

    public String getPartitaIva() {
        return partitaIva;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getDataInserimento() {
        return dataInserimento;
    }

    public LocalDate getDataUltimoContatto() {
        return dataUltimoContatto;
    }

    public Double getFatturatoAnnuale() {
        return fatturatoAnnuale;
    }

    public String getPec() {
        return pec;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getLogoAziendale() {
        return logoAziendale;
    }

    public TipoAzienda getTipo() {
        return tipo;
    }

    public String getEmailContatto() {
        return emailContatto;
    }

    public String getNomeContatto() {
        return nomeContatto;
    }

    public String getCognomeContatto() {
        return cognomeContatto;
    }

    public String getTelefonoContatto() {
        return telefonoContatto;
    }

    public Indirizzo getIndirizzoSedeLegale() {
        return indirizzoSedeLegale;
    }

    public Indirizzo getIndirizzoSedeOperativa() {
        return indirizzoSedeOperativa;
    }

}