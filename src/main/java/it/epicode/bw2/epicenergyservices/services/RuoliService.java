package it.epicode.bw2.epicenergyservices.services;

import it.epicode.bw2.epicenergyservices.dto.request.RuoliDTO;
import it.epicode.bw2.epicenergyservices.entities.Ruolo;
import it.epicode.bw2.epicenergyservices.exceptions.BadRequestException;
import it.epicode.bw2.epicenergyservices.exceptions.NotFoundException;
import it.epicode.bw2.epicenergyservices.repositories.RuoliRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RuoliService {

    private final RuoliRepository ruoliRepository;

    public RuoliService(RuoliRepository ruoliRepository) {
        this.ruoliRepository = ruoliRepository;
    }


    public List<Ruolo> getAllRuoli() {
        return ruoliRepository.findAll();
    }


    public Ruolo getRuoloById(Long id) {
        return ruoliRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Ruolo non trovato"));
    }


    @Transactional
    public Ruolo addRuolo(RuoliDTO payload) {
        // Verifica che il ruolo non esista già
        if (ruoliRepository.existsByRuolo(payload.ruolo())) {
            throw new BadRequestException("Ruolo '" + payload.ruolo() + "' già esiste");
        }

        Ruolo newRuolo = new Ruolo(payload.ruolo());
        Ruolo saved = this.ruoliRepository.save(newRuolo);
        System.out.println("Ruolo creato: " + saved.getRuolo());
        return saved;
    }


    @Transactional
    public Ruolo updateRuolo(Long id, RuoliDTO payload) {
        Ruolo ruolo = getRuoloById(id);
        
        // Verifica che il nuovo nome non sia già in uso (da un altro ruolo)
        if (!ruolo.getRuolo().equals(payload.ruolo()) && 
            ruoliRepository.existsByRuolo(payload.ruolo())) {
            throw new BadRequestException("Ruolo '" + payload.ruolo() + "' già esiste");
        }
        
        ruolo.setRuolo(payload.ruolo());
        return ruoliRepository.save(ruolo);
    }


    @Transactional
    public void deleteRuolo(Long id) {
        Ruolo ruolo = getRuoloById(id);
        ruoliRepository.delete(ruolo);
        System.out.println("Ruolo eliminato: " + ruolo.getRuolo());
    }

    public Ruolo findByRuolo(String ruolo) {
        return ruoliRepository.findByRuolo(ruolo)
            .orElseThrow(() -> new NotFoundException("Ruolo non trovato"));
    }

    public boolean existsByRuolo(String ruolo) {
        return ruoliRepository.existsByRuolo(ruolo);
    }
}