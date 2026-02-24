package it.epicode.bw2.epicenergyservices.services;

import it.epicode.bw2.epicenergyservices.entities.Indirizzo;
import it.epicode.bw2.epicenergyservices.exceptions.BadRequestException;
import it.epicode.bw2.epicenergyservices.exceptions.NotFoundException;
import it.epicode.bw2.epicenergyservices.repositories.SedeClienteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SedeClientiService {
    private final SedeClienteRepository sedeClienteRepository;
    private final IndirizziService indirizziService;

    @Autowired
    public SedeClientiService(SedeClienteRepository sedeClienteRepository,
                              IndirizziService indirizziService) {
        this.sedeClienteRepository = sedeClienteRepository;
        this.indirizziService = indirizziService;
    }

    public SedeCliente save(TipoSede tipoSede, Long idCliente, long indirizzoId) {

        Indirizzo indirizzo = this.indirizziService.findById(indirizzoId);

        if (this.sedeClienteRepository
                .existsByIdCliente(idCliente)) {
            throw new BadRequestException("esiste già una sede " + tipoSede + " per il cliente con ID " + idCliente + " su questo indirizzo");
        }

        SedeCliente nuovaSede = new SedeCliente(tipoSede, idCliente, indirizzo);
        SedeCliente saved = this.sedeClienteRepository.save(nuovaSede);

        log.info("sede Cliente (" + tipoSede + ") salvata con ID: " + saved.getId());
        return saved;
    }

    public SedeCliente findById(long id) {
        return this.sedeClienteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("sede Cliente con ID " + id + " non trovata"));
    }

    public void findByIdAndDelete(long id) {
        SedeCliente found = this.findById(id);
        this.sedeClienteRepository.delete(found);
        log.info("sede Cliente con ID " + id + " eliminata");
    }
}
