package it.epicode.bw2.epicenergyservices.controllers;

import it.epicode.bw2.epicenergyservices.entities.Utente;
import it.epicode.bw2.epicenergyservices.exceptions.ValidationException;
import it.epicode.bw2.epicenergyservices.services.RuoliService;
import it.epicode.bw2.epicenergyservices.services.UtentiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/utenti")
public class UtentiController {


    private final RuoliService ruoliService;
    private final UtentiService utentiService;

    @Autowired
    public UtentiController(RuoliService ruoliService, UtentiService utentiService) {
        this.ruoliService = ruoliService;
        this.utentiService = utentiService;
    }


    @PatchMapping("/ruoli"
    )
    @PreAuthorize("hasRole('ADMIN')")
    public Utente addRuoloUtente(@RequestBody @Validated Long idUtente, Long idRuolo, BindingResult valRes) {
        if (valRes.hasErrors()) {
            List<String> errList = valRes.getFieldErrors().stream().map(fieldError -> fieldError.getDefaultMessage()).toList();

            throw new ValidationException(errList);
        }
        return this.utentiService.addRuoloUtente(idUtente, idRuolo);
    }
}