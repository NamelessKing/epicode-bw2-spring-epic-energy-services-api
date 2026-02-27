package it.epicode.bw2.epicenergyservices.services;

import it.epicode.bw2.epicenergyservices.dto.request.FatturaDTO;
import it.epicode.bw2.epicenergyservices.dto.request.StatoFatturaDTO;
import it.epicode.bw2.epicenergyservices.dto.request.UpdateFatturaDTO;
import it.epicode.bw2.epicenergyservices.dto.response.ResponseFatturaDTO;
import it.epicode.bw2.epicenergyservices.entities.Cliente;
import it.epicode.bw2.epicenergyservices.entities.Fattura;
import it.epicode.bw2.epicenergyservices.entities.StatoFattura;
import it.epicode.bw2.epicenergyservices.entities.Utente;
import it.epicode.bw2.epicenergyservices.exceptions.BadRequestException;
import it.epicode.bw2.epicenergyservices.exceptions.NotFoundException;
import it.epicode.bw2.epicenergyservices.repositories.FatturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

    public ResponseFatturaDTO toResponseDTO(Fattura fattura) {
        return new ResponseFatturaDTO(
                fattura.getData(),
                fattura.getImporto(),
                fattura.getNumero(),
                fattura.getCliente().getId(),
                fattura.getUtente().getId(),
                fattura.getStato().getStato()
        );
    }

    //save
    public Fattura save(FatturaDTO payload, Utente utente, long idCliente) {

        if (fatturaRepository.existsByNumero(payload.numero())) {
            throw new BadRequestException("Il numero di fattura " + payload.numero() + " è gia stato assegnato.");

        }
        StatoFattura statoFromDb = this.statoFatturaService.findStato("EMESSA");
        Cliente clienteFromDB = this.clienteService.findClienteById(idCliente);
        if (clienteFromDB == null || !clienteFromDB.isAttivo()) throw new NotFoundException("Cliente non valido");

        Fattura fattura = new Fattura(
                payload.data(),
                payload.importo(),
                payload.numero(),
                statoFromDb,
                utente,
                clienteFromDB
        );


        return fatturaRepository.save(fattura);


    }

    //update
    public Fattura updateFattura(UpdateFatturaDTO payload, long idFattura) {

        Fattura fatturaFromDB = this.fatturaRepository.findFatturaById(idFattura);
        if (fatturaFromDB == null) throw new NotFoundException("Fattura non trovata");

        fatturaFromDB.setData(payload.data());
        fatturaFromDB.setImporto(payload.importo());

        return this.fatturaRepository.save(fatturaFromDB);
    }

    //update stato
    public Fattura setStatoFattura(Long idFattura, StatoFatturaDTO payload) {

        Fattura fatturaFromDb = this.fatturaRepository.findFatturaById(idFattura);

        if (fatturaFromDb == null) throw new NotFoundException("Fattura non trovata");

        StatoFattura statoFromDb = this.statoFatturaService.findStato(payload.stato());

        if (statoFromDb == null) throw new NotFoundException("Stato non presente in db");

        fatturaFromDb.setStato(statoFromDb);

        return this.fatturaRepository.save(fatturaFromDb);

    }

    public Page<ResponseFatturaDTO> filtraFatture(
            Long idCliente,
            String stato,
            Integer anno,
            Double min,
            Double max,
            Pageable pageable
    ) {
        Page<Fattura> result = null;

        if (idCliente != null) {
            result = fatturaRepository.findByClienteId(idCliente, pageable);
        } else if (stato != null) {
            result = fatturaRepository.findByStatoStato(stato, pageable);
        } else if (anno != null) {
            result = fatturaRepository.findByAnno(anno, pageable);
        } else if (min != null && max != null) {
            result = fatturaRepository.findByImportoBetween(BigDecimal.valueOf(min), BigDecimal.valueOf(max), pageable);
        } else if (result == null) {
            result = this.fatturaRepository.findAll(pageable);
        }

        return result.map(this::toResponseDTO);
    }


    public Fattura getFatturaByid(Long idfattura) {

        Fattura found = fatturaRepository.findFatturaById(idfattura);
        if (found == null) throw new NotFoundException("Fattura non trovata");

        return found;
    }


    public Page<Fattura> getFatture(Pageable pageable) {
        return this.fatturaRepository.findAll(pageable);
    }

    //delete fattura
    public void deleteFattura(long id) {
        Fattura fattura = fatturaRepository.findFatturaById(id);
        if (fattura == null) throw new NotFoundException("Fattura non trovata");
        fatturaRepository.delete(fattura);
    }
}
