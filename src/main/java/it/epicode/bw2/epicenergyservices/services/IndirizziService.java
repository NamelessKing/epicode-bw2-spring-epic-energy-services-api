package it.epicode.bw2.epicenergyservices.services;

import it.epicode.bw2.epicenergyservices.dto.response.IndirizziDTO;
import it.epicode.bw2.epicenergyservices.entities.Comune;
import it.epicode.bw2.epicenergyservices.entities.Indirizzo;
import it.epicode.bw2.epicenergyservices.exceptions.BadRequestException;
import it.epicode.bw2.epicenergyservices.exceptions.NotFoundException;
import it.epicode.bw2.epicenergyservices.repositories.ComuneRepository;
import it.epicode.bw2.epicenergyservices.repositories.IndirizziRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class IndirizziService {
    private final ComuniService comuniService;

    private final ComuneRepository comuneRepository;
    private final IndirizziRepository indirizziRepository;


    @Autowired
    public IndirizziService(ComuniService comuniService, ComuneRepository comuneRepository, IndirizziRepository indirizziRepository) {
        this.comuniService = comuniService;
        this.indirizziRepository = indirizziRepository;
        this.comuneRepository = comuneRepository;

    }

    public Indirizzo save(IndirizziDTO payload) {
        Comune found = comuniService.findById(payload.comuneId());

        if (indirizziRepository.existsByViaAndCivicoAndLocalitaAndCapAndComune(payload.via(), payload.civico(), payload.localita(), payload.cap(), found)) {
            throw new BadRequestException("esiste già l'indirizzo");
        }
        Indirizzo newIndirizzo = new Indirizzo(payload.via(), payload.civico(), payload.localita(), payload.cap(), found);
        Indirizzo saved = this.indirizziRepository.save(newIndirizzo);
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
        log.info("indirizzo con ID: " + id + " eliminato");
    }

    public List<Indirizzo> findAll() {
        return this.indirizziRepository.findAll();
    }

    public Indirizzo findByIdAndUpdate(long id, IndirizziDTO payload) {
        Indirizzo found = this.findById(id);
        found.setVia(payload.via());
        found.setCivico(String.valueOf(payload.civico()));
        found.setLocalita(payload.localita());
        found.setCap(String.valueOf(payload.cap()));
        Comune comune = comuneRepository.findById(payload.comuneId()).orElseThrow(() -> new NotFoundException("comune non trovato"));
        found.setComune(comune);
        return this.indirizziRepository.save(found);
    }

}
