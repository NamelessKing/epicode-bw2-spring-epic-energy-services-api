package it.epicode.bw2.epicenergyservices.runners;

import it.epicode.bw2.epicenergyservices.dto.request.RegisterDTO;
import it.epicode.bw2.epicenergyservices.dto.request.RuoliDTO;
import it.epicode.bw2.epicenergyservices.dto.request.StatoFatturaDTO;
import it.epicode.bw2.epicenergyservices.entities.Ruolo;
import it.epicode.bw2.epicenergyservices.entities.StatoFattura;
import it.epicode.bw2.epicenergyservices.entities.Utente;
import it.epicode.bw2.epicenergyservices.services.ImportAnagraficheService;
import it.epicode.bw2.epicenergyservices.services.FatturaService;
import it.epicode.bw2.epicenergyservices.services.RuoliService;
import it.epicode.bw2.epicenergyservices.services.StatoFatturaService;
import it.epicode.bw2.epicenergyservices.services.UtentiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * - Importa Province/Comuni da CSV (se abilitato via app.import.enabled)
 * - Crea i ruoli di default (USER, ADMIN, MANAGER, TECHNICIAN) se non esistono
 * - Crea un utente admin di test se non esiste
 */
@Component
@Order(1)
public class Runner implements CommandLineRunner {

    private final RuoliService ruoliService;
    private final UtentiService utentiService;
    private final StatoFatturaService statoFatturaService;

    @Value("${app.seed.enabled:true}")
    private boolean seedEnabled;
    
    @Value("${admin.username}")
    private String adminUsername;
    @Value("${admin.name}")
    private String adminName;
    @Value("${admin.lastname}")
    private String adminLastName;
    @Value("${admin.email}")
    private String adminEmail;
    @Value("${admin.pwd}")
    private String adminPwd;

    @Autowired
    public Runner(RuoliService ruoliService, UtentiService utentiService, StatoFatturaService statoFatturaService) {
        this.ruoliService = ruoliService;
        this.utentiService = utentiService;
        this.statoFatturaService = statoFatturaService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!seedEnabled) {
            System.out.println("Seeding disabilitato (app.seed.enabled=false)");
            return;
        }
        
        System.out.println("Inizio seeding dei dati...");
        
        // Crea ruolo USER
        if (!this.ruoliService.existsByRuolo("USER")) {
            Ruolo ruoloUser = this.ruoliService.addRuolo(new RuoliDTO("USER"));
            System.out.println("Ruolo creato: " + ruoloUser);
        }

        // Crea ruolo ADMIN
        if (!this.ruoliService.existsByRuolo("ADMIN")) {
            Ruolo ruoloAdmin = this.ruoliService.addRuolo(new RuoliDTO("ADMIN"));
            System.out.println("Ruolo creato: " + ruoloAdmin);
        }

        // Crea ruolo MANAGER
        if (!this.ruoliService.existsByRuolo("MANAGER")) {
            Ruolo ruoloManager = this.ruoliService.addRuolo(new RuoliDTO("MANAGER"));
            System.out.println("Ruolo creato: " + ruoloManager);
        }

        // Crea ruolo TECHNICIAN
        if (!this.ruoliService.existsByRuolo("TECHNICIAN")) {
            Ruolo ruoloTechnician = this.ruoliService.addRuolo(new RuoliDTO("TECHNICIAN"));
            System.out.println("Ruolo creato: " + ruoloTechnician);
        }

        // Crea utente admin di default
        boolean utenteAdminExistFromDB = this.utentiService.existByEmail(adminEmail);
        if (!utenteAdminExistFromDB) {
            RegisterDTO admin = new RegisterDTO(adminUsername, adminEmail, adminPwd, adminName, adminLastName);
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