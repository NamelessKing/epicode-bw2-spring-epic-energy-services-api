package it.epicode.bw2.epicenergyservices.controllers;

import it.epicode.bw2.epicenergyservices.dto.request.FatturaDTO;
import it.epicode.bw2.epicenergyservices.dto.response.ResponseFatturaDTO;
import it.epicode.bw2.epicenergyservices.entities.Fattura;
import it.epicode.bw2.epicenergyservices.entities.StatoFattura;
import it.epicode.bw2.epicenergyservices.entities.Utente;
import it.epicode.bw2.epicenergyservices.services.FatturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fatture")
public class FatturaController {

    private final FatturaService fatturaService;

    @Autowired
    public FatturaController(FatturaService fatturaService) {
        this.fatturaService = fatturaService;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/{idCliente}")
    public Fattura saveFattura(@RequestBody @Validated FatturaDTO payload,
                               @PathVariable long idCliente,
                               @AuthenticationPrincipal Utente utente) {

        return this.fatturaService.save(payload, utente, idCliente);
    }

    //endpoint per modificare una fattura(data e importo)


    //endpoint per modificare lo stato una fattura
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping
    public Page<ResponseFatturaDTO> filtraFatture(
            @RequestParam(required = false) Long idCliente,
            @RequestParam(required = false) String stato,
            @RequestParam(required = false) Integer anno,
            @RequestParam(required = false) double min,
            @RequestParam(required = false) double max,
            @PageableDefault(page = 0, size = 10)
            Pageable pageable
    ) {
        return fatturaService.filtraFatture(idCliente, stato, anno, min, max, pageable);

    }
}
