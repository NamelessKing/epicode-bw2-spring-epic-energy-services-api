//package it.epicode.bw2.epicenergyservices.controllers;
//
//import it.epicode.bw2.epicenergyservices.dto.request.FatturaDTO;
//import it.epicode.bw2.epicenergyservices.entities.Fattura;
//import it.epicode.bw2.epicenergyservices.entities.Utente;
//import it.epicode.bw2.epicenergyservices.services.FatturaService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/fattura")
//public class FatturaController {
//
//    private final FatturaService fatturaService;
//
//    @Autowired
//    public FatturaController(FatturaService fatturaService) {
//        this.fatturaService = fatturaService;
//    }
//
//    @PreAuthorize("hasAuthority('ADMIN')")
//    @PostMapping("/{idCliente}")
//    public Fattura saveFattura(@RequestBody @Validated FatturaDTO payload,
//                               @PathVariable long idCliente,
//                               @AuthenticationPrincipal Utente utente) {
//
//        return this.fatturaService.save(payload, utente, idCliente);
//    }
//}
