package it.epicode.bw2.epicenergyservices.runners;

import it.epicode.bw2.epicenergyservices.dto.request.RegisterDTO;
import it.epicode.bw2.epicenergyservices.dto.request.RuoliDTO;
import it.epicode.bw2.epicenergyservices.entities.Ruolo;
import it.epicode.bw2.epicenergyservices.entities.Utente;
import it.epicode.bw2.epicenergyservices.services.ImportAnagraficheService;
import it.epicode.bw2.epicenergyservices.services.RuoliService;
import it.epicode.bw2.epicenergyservices.services.UtentiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * - Importa Province/Comuni da CSV (se abilitato via app.import.enabled)
 * - Crea i ruoli di default (USER, ADMIN, MANAGER, TECHNICIAN) se non esistono
 * - Crea un utente admin di test se non esiste
 */
@Component
public class Runner implements CommandLineRunner {

    private final RuoliService ruoliService;
    private final UtentiService utentiService;
    private final ImportAnagraficheService importAnagraficheService;

    @Value("${app.import.enabled:false}")
    private boolean importEnabled;

    @Autowired
    public Runner(RuoliService ruoliService,
                  UtentiService utentiService,
                  ImportAnagraficheService importAnagraficheService) {
        this.ruoliService = ruoliService;
        this.utentiService = utentiService;
        this.importAnagraficheService = importAnagraficheService;
    }

    @Override
    public void run(String... args) {
        System.out.println("Inizio seeding dei dati...");

        // Import Province/Comuni da CSV (manuale tramite flag)
        if (importEnabled) {
            System.out.println(">>> Import CSV abilitato (app.import.enabled=true)");
            importAnagraficheService.importaTutto();
            System.out.println(">>> Import CSV completato");
        } else {
            System.out.println(">>> Import CSV disabilitato (app.import.enabled=false)");
        }

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
        if (!this.utentiService.existByEmail("betta@pcq.it")) {
            RegisterDTO admin = new RegisterDTO("bettapcq", "betta@pcq.it", "bbEli8!123", "betta", "pcq");
            Utente utenteAdmin = this.utentiService.addUtente(admin, "ADMIN");
            System.out.println("Utente admin creato: " + utenteAdmin.getUsername());
        }

        System.out.println("Seeding completato!");
    }
}