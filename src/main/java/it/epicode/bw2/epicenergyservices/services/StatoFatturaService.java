package it.epicode.bw2.epicenergyservices.services;

import it.epicode.bw2.epicenergyservices.dto.request.StatoFatturaDTO;
import it.epicode.bw2.epicenergyservices.entities.StatoFattura;
import it.epicode.bw2.epicenergyservices.exceptions.BadRequestException;
import it.epicode.bw2.epicenergyservices.exceptions.NotFoundException;
import it.epicode.bw2.epicenergyservices.repositories.StatoFatturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class StatoFatturaService {

    private final StatoFatturaRepository statoFatturaRepository;

    @Autowired
    public StatoFatturaService(StatoFatturaRepository statoFatturaRepository) {
        this.statoFatturaRepository = statoFatturaRepository;
    }

    public StatoFatturaDTO toResponseDTO(StatoFattura statoFattura) {
        return new StatoFatturaDTO(
                statoFattura.getStato()
        );
    }

    //save
    public StatoFattura save(StatoFatturaDTO payload) {
        if (statoFatturaRepository.existsByStato(payload.stato())) {
            throw new BadRequestException("Lo stato fattura inserito esiste gia.");
        }

        StatoFattura nuovoStato = new StatoFattura(payload.stato());
        return statoFatturaRepository.save(nuovoStato);
    }

    //find by stato
    public StatoFattura findStato(String stato) {

        return statoFatturaRepository.findByStato(stato).orElseThrow(() ->
                new NotFoundException("Stato fattura non trovato."));


    }

    //find by id
    public StatoFattura findById(long idStato) {
        return statoFatturaRepository.findById(idStato).orElseThrow(() ->
                new NotFoundException("Lo stato richiesto non esiste"));
    }

    //get all
    public Page<StatoFattura> getAllStatiFattura(Pageable pageable) {
        return statoFatturaRepository.findAll(pageable);
    }

    //controlla se esiste
    public boolean existByStato(String stato) {

        return statoFatturaRepository.existsByStato(stato);
    }

    //elimina stato fattura
    public void eliminaStatoFatturaConId(long idStato) {
        StatoFattura statoDaEliminare = statoFatturaRepository.findById(idStato)
                .orElseThrow(() -> new NotFoundException("Stato fattura non trovato."));

        statoFatturaRepository.delete(statoDaEliminare);
    }

    public void eliminaStatoFatturaConNomeStato(String stato) {
        StatoFattura statoDaEliminare = statoFatturaRepository.findByStato(stato)
                .orElseThrow(() -> new NotFoundException("Stato fattura non trovato."));

        statoFatturaRepository.delete(statoDaEliminare);
    }

}
