package it.epicode.bw2.epicenergyservices.services;

import it.epicode.bw2.epicenergyservices.dto.request.FatturaDTO;
import it.epicode.bw2.epicenergyservices.entities.Cliente;
import it.epicode.bw2.epicenergyservices.entities.Fattura;
import it.epicode.bw2.epicenergyservices.entities.StatoFattura;
import it.epicode.bw2.epicenergyservices.entities.Utente;
import it.epicode.bw2.epicenergyservices.exceptions.BadRequestException;
import it.epicode.bw2.epicenergyservices.exceptions.NotFoundException;
import it.epicode.bw2.epicenergyservices.repositories.FatturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FatturaService {

    private final FatturaRepository fatturaRepository;
    private final StatoFatturaService statoFatturaService;
    private final ClienteService clienteService;

    @Autowired
    public FatturaService(FatturaRepository fatturaRepository, StatoFatturaService statoFatturaService, ClienteService clienteService) {
        this.fatturaRepository = fatturaRepository;
        this.statoFatturaService = statoFatturaService;
        this.clienteService = clienteService;
    }

    //save
    public Fattura save(FatturaDTO payload, Utente utente, long idCliente) {

        if (fatturaRepository.existsByNumero(payload.numero())) {
            throw new BadRequestException("Il numero di fattura " + payload.numero() + " è gia stato assegnato.");

        }
        StatoFattura statoFromDb = this.statoFatturaService.findStato("EMESSA");
        Cliente clienteFromDB = this.clienteService.findClienteById(idCliente);
        if (clienteFromDB == null) throw new NotFoundException("Cliente non valido");

        Fattura fattura = new Fattura();
        fattura.setData(payload.data());
        fattura.setImporto(payload.importo());
        fattura.setNumero(payload.numero());
        fattura.setStato(statoFromDb);
        fattura.setUtente(utente);
        fattura.setCliente(clienteFromDB);

        return fatturaRepository.save(fattura);
    }

    //update

    //update stato


    //delete
}
