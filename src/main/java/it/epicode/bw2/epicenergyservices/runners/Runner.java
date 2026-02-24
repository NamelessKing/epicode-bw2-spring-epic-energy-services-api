package it.epicode.bw2.epicenergyservices.runners;

import it.epicode.bw2.epicenergyservices.dto.request.RegisterDTO;
import it.epicode.bw2.epicenergyservices.dto.request.RuoliDTO;
import it.epicode.bw2.epicenergyservices.entities.Ruolo;
import it.epicode.bw2.epicenergyservices.entities.Utente;
import it.epicode.bw2.epicenergyservices.services.RuoliService;
import it.epicode.bw2.epicenergyservices.services.UtentiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Runner implements CommandLineRunner {

    private final RuoliService ruoliService;
    private final UtentiService utentiService;

    @Autowired
    public Runner(RuoliService ruoliService, UtentiService utentiService) {
        this.ruoliService = ruoliService;
        this.utentiService = utentiService;
    }


    @Override
    public void run(String... args) throws Exception {

        boolean ruoloUserExistInBd = this.ruoliService.existsByRuolo("USER");
        if (!ruoloUserExistInBd) {
            Ruolo ruoloUser = this.ruoliService.addRuolo(new RuoliDTO("USER"));
            System.out.println(ruoloUser.toString());
        }

        boolean ruoloAdminExistInBd = this.ruoliService.existsByRuolo("ADMIN");
        if (!ruoloAdminExistInBd) {
            Ruolo ruoloAdmin = this.ruoliService.addRuolo(new RuoliDTO("ADMIN"));
            System.out.println(ruoloAdmin.toString());
        }

        boolean utenteAdminExistFromDB = this.utentiService.existByEmail("betta@pcq.it");
        if (!utenteAdminExistFromDB) {
            RegisterDTO admin = new RegisterDTO("bettapcq", "betta@pcq.it", "bbEli8!123", "betta", "pcq");
            Utente utenteAdmin = this.utentiService.addUtente(admin, "ADMIN");

        }
    }


}