package it.epicode.bw2.epicenergyservices.services;

import it.epicode.bw2.epicenergyservices.dto.response.StatoFatturaDTO;
import it.epicode.bw2.epicenergyservices.entities.StatoFattura;
import it.epicode.bw2.epicenergyservices.exceptions.BadRequestException;
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
        statoFatturaRepository.findByStato(payload.stato()).ifPresent(s -> {
            throw new BadRequestException("Lo stato " + payload.stato() + " è gia presente");
        });

        StatoFattura stato = new StatoFattura();
        stato.setStato(payload.stato());

        return statoFatturaRepository.save(stato);
    }


}
