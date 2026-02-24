package it.epicode.bw2.epicenergyservices.runners;

import it.epicode.bw2.epicenergyservices.dto.request.RegisterDTO;
import it.epicode.bw2.epicenergyservices.dto.request.RuoliDTO;
import it.epicode.bw2.epicenergyservices.dto.request.StatoFatturaDTO;
import it.epicode.bw2.epicenergyservices.entities.Ruolo;
import it.epicode.bw2.epicenergyservices.entities.StatoFattura;
import it.epicode.bw2.epicenergyservices.entities.Utente;
import it.epicode.bw2.epicenergyservices.services.FatturaService;
import it.epicode.bw2.epicenergyservices.services.RuoliService;
import it.epicode.bw2.epicenergyservices.services.StatoFatturaService;
import it.epicode.bw2.epicenergyservices.services.UtentiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * - Crea i ruoli di default (USER, ADMIN, MANAGER, TECHNICIAN) se non esistono
 * - Crea un utente admin di test se non esiste
 *
 */
@Component
public class Runner implements CommandLineRunner {

    private final RuoliService ruoliService;
    private final UtentiService utentiService;
    private final StatoFatturaService statoFatturaService;

    @Autowired
    public Runner(RuoliService ruoliService, UtentiService utentiService, StatoFatturaService statoFatturaService) {
        this.ruoliService = ruoliService;
        this.utentiService = utentiService;
        this.statoFatturaService = statoFatturaService;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Inizio seeding dei dati...");

        // Crea ruolo USER
        boolean ruoloUserExistInBd = this.ruoliService.existsByRuolo("USER");
        if (!ruoloUserExistInBd) {
            Ruolo ruoloUser = this.ruoliService.addRuolo(new RuoliDTO("USER"));
            System.out.println("Ruolo creato: " + ruoloUser.toString());
        }

        // Crea ruolo ADMIN
        boolean ruoloAdminExistInBd = this.ruoliService.existsByRuolo("ADMIN");
        if (!ruoloAdminExistInBd) {
            Ruolo ruoloAdmin = this.ruoliService.addRuolo(new RuoliDTO("ADMIN"));
            System.out.println("Ruolo creato: " + ruoloAdmin.toString());
        }

        // Crea ruolo MANAGER
        boolean ruoloManagerExistInBd = this.ruoliService.existsByRuolo("MANAGER");
        if (!ruoloManagerExistInBd) {
            Ruolo ruoloManager = this.ruoliService.addRuolo(new RuoliDTO("MANAGER"));
            System.out.println("Ruolo creato: " + ruoloManager.toString());
        }

        //  Crea ruolo TECHNICIAN
        boolean ruoloTechnicianExistInBd = this.ruoliService.existsByRuolo("TECHNICIAN");
        if (!ruoloTechnicianExistInBd) {
            Ruolo ruoloTechnician = this.ruoliService.addRuolo(new RuoliDTO("TECHNICIAN"));
            System.out.println("Ruolo creato: " + ruoloTechnician.toString());
        }

        // Crea utente admin di default
        boolean utenteAdminExistFromDB = this.utentiService.existByEmail("betta@pcq.it");
        if (!utenteAdminExistFromDB) {
            RegisterDTO admin = new RegisterDTO("bettapcq", "betta@pcq.it", "bbEli8!123", "betta", "pcq");
            Utente utenteAdmin = this.utentiService.addUtente(admin, "ADMIN");
            System.out.println("Utente admin creato: " + utenteAdmin.getUsername());
        }

        //crea stato fattura
        boolean statoFatturaExistFromDB = this.statoFatturaService.existByStato("EMESSA");
        if (!statoFatturaExistFromDB) {
            StatoFattura statoEmessa = this.statoFatturaService.save(new StatoFatturaDTO("EMESSA"));
            System.out.println("Stato creato: " + statoEmessa.toString());
        }


        System.out.println("Seeding completato!");
    }
}