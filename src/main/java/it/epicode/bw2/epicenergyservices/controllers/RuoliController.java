package it.epicode.bw2.epicenergyservices.controllers;

import it.epicode.bw2.epicenergyservices.dto.request.RuoliDTO;
import it.epicode.bw2.epicenergyservices.entities.Ruolo;
import it.epicode.bw2.epicenergyservices.exceptions.ValidationException;
import it.epicode.bw2.epicenergyservices.services.RuoliService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(name = "/ruoli")
public class RuoliController {


    private final RuoliService ruoliService;

    @Autowired
    public RuoliController(RuoliService ruoliService) {
        this.ruoliService = ruoliService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ADMIN')")
    public Ruolo addRuolo(@RequestBody @Validated RuoliDTO payload, BindingResult valRes) {
        if (valRes.hasErrors()) {
            List<String> errList = valRes.getFieldErrors()
                    .stream()
                    .map(fieldError -> fieldError.getDefaultMessage()).toList();

            throw new ValidationException(errList);
        }
        return this.ruoliService.addRuolo(payload);
    }
}
