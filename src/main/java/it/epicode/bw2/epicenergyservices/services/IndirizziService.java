package it.epicode.bw2.epicenergyservices.services;

import it.epicode.bw2.epicenergyservices.entities.Indirizzo;
import it.epicode.bw2.epicenergyservices.exceptions.NotFoundException;
import it.epicode.bw2.epicenergyservices.repositories.IndirizziRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class IndirizziService {

    private final IndirizziRepository indirizziRepository;

    @Autowired
    public IndirizziService(IndirizziRepository indirizziRepository) {
        this.indirizziRepository = indirizziRepository;
    }

    public Indirizzo save(Indirizzo indirizzo) {
        Indirizzo saved = this.indirizziRepository.save(indirizzo);
        log.info("indirizzo salvato con ID: " + saved.getId());
        return saved;
    }

    public Indirizzo findById(long id) {
        return this.indirizziRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("indirizzo con Id: " + id + " non trovato"));
    }

    public void findByIdAndDelete(long id) {
        Indirizzo found = this.findById(id);
        this.indirizziRepository.delete(found);
        log.info("indirizzo con ID " + id + " eliminato");
    }

}
