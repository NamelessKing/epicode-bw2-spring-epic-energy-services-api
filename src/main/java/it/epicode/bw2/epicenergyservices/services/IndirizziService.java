package it.epicode.bw2.epicenergyservices.services;

import it.epicode.bw2.epicenergyservices.dto.request.UpdateIndirizziDTO;
import it.epicode.bw2.epicenergyservices.dto.response.IndirizziDTO;
import it.epicode.bw2.epicenergyservices.entities.Comune;
import it.epicode.bw2.epicenergyservices.entities.Indirizzo;
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

    private final ComuneRepository comuneRepository;
    private final IndirizziRepository indirizziRepository;
    private final ComuniService comuniService;


    @Autowired
    public IndirizziService(ComuneRepository comuneRepository, IndirizziRepository indirizziRepository, ComuniService comuniService) {
        this.indirizziRepository = indirizziRepository;
        this.comuneRepository = comuneRepository;

        this.comuniService = comuniService;
    }

    public Indirizzo save(IndirizziDTO payload, Long comuneId) {

        Comune comune = comuniService.findById(comuneId);
        if (comune == null) throw new NotFoundException("Comune non presente");

        Indirizzo saved = new Indirizzo(
                payload.via(),
                payload.civico(),
                payload.localita(),
                payload.cap(),
                comune
        );

        indirizziRepository.save(saved);

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

    public Indirizzo findByIdAndUpdate(long id, UpdateIndirizziDTO body) {
        Indirizzo found = this.findById(id);
        found.setVia(body.via());
        found.setCivico(String.valueOf(body.civico()));
        found.setLocalita(body.localita());
        found.setCap(String.valueOf(body.cap()));
        Comune comune = comuneRepository.findById(body.comuneId()).orElseThrow(() -> new NotFoundException("comune non trovato"));
        found.setComune(comune);
        return this.indirizziRepository.save(found);
    }

}
