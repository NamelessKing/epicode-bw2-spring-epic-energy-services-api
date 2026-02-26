package it.epicode.bw2.epicenergyservices.services;

import it.epicode.bw2.epicenergyservices.dto.request.StatoFatturaDTO;
import it.epicode.bw2.epicenergyservices.entities.StatoFattura;
import it.epicode.bw2.epicenergyservices.exceptions.BadRequestException;
import it.epicode.bw2.epicenergyservices.exceptions.NotFoundException;
import it.epicode.bw2.epicenergyservices.repositories.StatoFatturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatoFatturaService {

    private final StatoFatturaRepository statoFatturaRepository;

    @Autowired
    public StatoFatturaService(StatoFatturaRepository statoFatturaRepository) {
        this.statoFatturaRepository = statoFatturaRepository;
    }

    //save
    public StatoFattura save(StatoFatturaDTO payload) {
        StatoFattura stato = statoFatturaRepository.findByStato(payload.stato());
        if (stato != null) throw new BadRequestException("Lo stato esiste gia");

        StatoFattura NuovoStato = new StatoFattura(payload.stato());

        return statoFatturaRepository.save(NuovoStato);
    }

    //find
    public StatoFattura findStato(String stato) {

        StatoFattura statoTrovato = statoFatturaRepository.findByStato(stato);
        if (statoTrovato == null) throw new NotFoundException("stato non trovato");
        return statoTrovato;
    }

    //find by id
    public StatoFattura findById(Long idStato) {
        StatoFattura stato = statoFatturaRepository.findByIdStato(idStato);

        if (stato == null) throw new NotFoundException("Stato fattura inesistente.");
        return stato;
    }

    //
    public boolean existByStato(String stato) {

        return statoFatturaRepository.existsByStato(stato);
    }

}
