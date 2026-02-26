package it.epicode.bw2.epicenergyservices.controllers;

import it.epicode.bw2.epicenergyservices.dto.request.StatoFatturaDTO;
import it.epicode.bw2.epicenergyservices.entities.StatoFattura;
import it.epicode.bw2.epicenergyservices.services.StatoFatturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("statiFatture")
public class StatoFatturaController {

    private final StatoFatturaService statoFatturaService;

    @Autowired
    public StatoFatturaController(StatoFatturaService statoFatturaService) {
        this.statoFatturaService = statoFatturaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public StatoFatturaDTO addStatoFattura(@RequestBody @Validated StatoFatturaDTO payload) {
        StatoFattura statoFattura = statoFatturaService.save(payload);
        return this.statoFatturaService.toResponseDTO(statoFattura);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Page<StatoFatturaDTO> getAllStatiFattura(@RequestParam Pageable pageable) {
        Page<StatoFattura> risultati = statoFatturaService.getAllStatiFattura(pageable);
        return risultati.map(statoFatturaService::toResponseDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteStatoConId(@PathVariable long id) {
        statoFatturaService.eliminaStatoFatturaConId(id);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteStatoConNomeStato(@RequestParam String stato) {
        statoFatturaService.eliminaStatoFatturaConNomeStato(stato);
    }
}
