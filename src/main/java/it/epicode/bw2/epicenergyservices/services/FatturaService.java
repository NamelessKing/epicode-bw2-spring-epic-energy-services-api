package it.epicode.bw2.epicenergyservices.services;

import it.epicode.bw2.epicenergyservices.dto.response.FatturaDTO;
import it.epicode.bw2.epicenergyservices.entities.Fattura;
import it.epicode.bw2.epicenergyservices.exceptions.BadRequestException;
import it.epicode.bw2.epicenergyservices.repositories.FatturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FatturaService {

    private final FatturaRepository fatturaRepository;

    @Autowired
    public FatturaService(FatturaRepository fatturaRepository) {
        this.fatturaRepository = fatturaRepository;
    }

    //save
    public Fattura save(FatturaDTO payload) {

        if (fatturaRepository.existsByNumero(payload.numero())) {
            throw new BadRequestException("Il numero di fattura " + payload.numero() + " è gia stato assegnato.");

        }

        Fattura fattura = new Fattura();
        fattura.setData(payload.data());
        fattura.setImporto(payload.importo());
        fattura.setNumero(payload.numero());

        return fatturaRepository.save(fattura);
    }

    //update

    //update stato
    

    //delete
}
