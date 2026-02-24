package it.epicode.bw2.epicenergyservices.services;

import it.epicode.bw2.epicenergyservices.dto.request.RuoliDTO;
import it.epicode.bw2.epicenergyservices.entities.Ruolo;
import it.epicode.bw2.epicenergyservices.exceptions.NotFoundException;
import it.epicode.bw2.epicenergyservices.repositories.RuoliRepository;
import org.springframework.stereotype.Service;

@Service
public class RuoliService {

    private final RuoliRepository ruoliRepository;

    public RuoliService(RuoliRepository ruoliRepository) {
        this.ruoliRepository = ruoliRepository;
    }


    public Ruolo findByRuolo(String ruolo) {
        return ruoliRepository.findByRuolo(ruolo).orElseThrow(() -> new NotFoundException("Ruolo non trovato"));
    }

    public Ruolo addRuolo(RuoliDTO payload) {

        Ruolo newRuolo = new Ruolo(payload.ruolo());

        this.ruoliRepository.save(newRuolo);
        return newRuolo;
    }

    public boolean existsByRuolo(String ruolo) {
        return ruoliRepository.existsByRuolo(ruolo);
    }
}
