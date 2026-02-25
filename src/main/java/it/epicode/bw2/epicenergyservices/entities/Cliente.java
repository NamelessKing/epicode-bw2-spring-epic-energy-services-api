package it.epicode.bw2.epicenergyservices.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;


import java.time.LocalDate;

@Entity
@Table(name = "cliente")

public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "status_cliente")
    private boolean stato = true;

    @Column(nullable = false)
    @NotBlank(message = "Ragione sociale obbligatoria")
    private String ragioneSociale;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Partita iva obbligatoria")
    private String partitaIva;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Email cliente obbligatoria")
    @Email(message = "Formato email cliente non valida")
    private String email;

    @Column(nullable = false)
    private LocalDate dataInserimento;

    @Column(nullable = true)
    private LocalDate dataUltimoContatto;

    @Column(nullable = false)
    @Positive(message = "Il fatturato annuale deve essere maggiore di zero")
    private double fatturatoAnnuale;

    @Column(nullable = false, unique = true)
    @Email(message = "Formato pec non valido")
    private String pec;

    @Column
    private String telefono;

    @Column
    private String logoAziendale;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoAzienda tipo;

    @Column(nullable = false, unique = true)
    @Email(message = "Formato email contatto non valido")
    private String emailContatto;

    @Column(nullable = false)
    @NotBlank(message = "Nome contatto obbligatorio")
    private String nomeContatto;

    @Column(nullable = false)
    @NotBlank(message = "Cognome contatto obbligatorio")
    private String cognomeContatto;

    @Column(nullable = false)
    @NotBlank(message = "Telefono contatto obbligatorio")
    private String telefonoContatto;

    //relazione OneToOne con id_sede_legale
    @OneToOne
    @JoinColumn(name = "id_sede_legale", nullable = false)
    private Indirizzo indirizzoSedeLegale;

    //relazione OneToOne con id_sede_operativa
    @OneToOne
    @JoinColumn(name = "id_sede_operativa")
    private Indirizzo indirizzoSedeOperativa;

    public Cliente() {
    }


    public Cliente(String ragioneSociale,
                   String partitaIva,
                   String email,
                   double fatturatoAnnuale,
                   String pec,
                   String telefono,
                   TipoAzienda tipo,
                   String emailContatto,
                   String nomeContatto,
                   String cognomeContatto,
                   String telefonoContatto,
                   Indirizzo sedeLegale
    ) {
        this.ragioneSociale = ragioneSociale;
        this.partitaIva = partitaIva;
        this.email = email;
        this.dataInserimento = LocalDate.now();
        this.dataUltimoContatto = LocalDate.now();
        this.fatturatoAnnuale = fatturatoAnnuale;
        this.pec = pec;
        this.telefono = telefono;
        this.logoAziendale = "https://placebear.com/200/200";
        this.tipo = tipo;
        this.emailContatto = emailContatto;
        this.nomeContatto = nomeContatto;
        this.cognomeContatto = cognomeContatto;
        this.telefonoContatto = telefonoContatto;
        this.indirizzoSedeLegale = sedeLegale;

    }


//    public Cliente(
//            String ragioneSociale,
//            String partitaIva,
//            String email,
//            double fatturatoAnnuale,
//            String pec,
//            String telefono,
//            TipoAzienda tipo,
//            LocalDate dataInserimento,
//            String emailContatto,
//            String nomeContatto,
//            String cognomeContatto,
//            String telefonoContatto
//    ) {
//        this.ragioneSociale = ragioneSociale;
//        this.partitaIva = partitaIva;
//        this.email = email;
//        this.fatturatoAnnuale = fatturatoAnnuale;
//        this.pec = pec;
//        this.telefono = telefono;
//        this.tipo = tipo;
//        this.dataInserimento = dataInserimento;
//        this.emailContatto = emailContatto;
//        this.nomeContatto = nomeContatto;
//        this.cognomeContatto = cognomeContatto;
//        this.telefonoContatto = telefonoContatto;
//    }


    public String getRagioneSociale() {
        return ragioneSociale;
    }

    public void setRagioneSociale(String ragioneSociale) {
        this.ragioneSociale = ragioneSociale;
    }

    public long getId() {
        return id;
    }

    public String getPartitaIva() {
        return partitaIva;
    }

    public void setPartitaIva(String partitaIva) {
        this.partitaIva = partitaIva;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDataInserimento() {
        return dataInserimento;
    }

    public void setDataInserimento(LocalDate dataInserimento) {
        this.dataInserimento = dataInserimento;
    }

    public LocalDate getDataUltimoContatto() {
        return dataUltimoContatto;
    }

    public void setDataUltimoContatto(LocalDate dataUltimoContatto) {
        this.dataUltimoContatto = dataUltimoContatto;
    }

    public double getFatturatoAnnuale() {
        return fatturatoAnnuale;
    }

    public void setFatturatoAnnuale(double fatturatoAnnuale) {
        this.fatturatoAnnuale = fatturatoAnnuale;
    }

    public String getPec() {
        return pec;
    }

    public void setPec(String pec) {
        this.pec = pec;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getLogoAziendale() {
        return logoAziendale;
    }

    public void setLogoAziendale(String logoAziendale) {
        this.logoAziendale = logoAziendale;
    }

    public TipoAzienda getTipo() {
        return tipo;
    }

    public void setTipo(TipoAzienda tipo) {
        this.tipo = tipo;
    }

    public String getEmailContatto() {
        return emailContatto;
    }

    public void setEmailContatto(String emailContatto) {
        this.emailContatto = emailContatto;
    }

    public String getNomeContatto() {
        return nomeContatto;
    }

    public void setNomeContatto(String nomeContatto) {
        this.nomeContatto = nomeContatto;
    }

    public String getCognomeContatto() {
        return cognomeContatto;
    }

    public void setCognomeContatto(String cognomeContatto) {
        this.cognomeContatto = cognomeContatto;
    }

    public String getTelefonoContatto() {
        return telefonoContatto;
    }

    public void setTelefonoContatto(String telefonoContatto) {
        this.telefonoContatto = telefonoContatto;
    }

    public Indirizzo getIndirizzoSedeLegale() {
        return indirizzoSedeLegale;
    }

    public void setIndirizzoSedeLegale(Indirizzo indirizzoSedeLegale) {
        this.indirizzoSedeLegale = indirizzoSedeLegale;
    }

    public Indirizzo getIndirizzoSedeOperativa() {
        return indirizzoSedeOperativa;
    }

    public void setIndirizzoSedeOperativa(Indirizzo indirizzoSedeOperativa) {
        this.indirizzoSedeOperativa = indirizzoSedeOperativa;
    }

    public boolean isAttivo() {
        return stato;
    }

    public void setStato(boolean stato) {
        this.stato = stato;
    }
}
