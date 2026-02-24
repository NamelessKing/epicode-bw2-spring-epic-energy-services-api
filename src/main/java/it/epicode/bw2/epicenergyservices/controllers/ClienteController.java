package it.epicode.bw2.epicenergyservices.controllers;

import it.epicode.bw2.epicenergyservices.dto.request.ClienteDTO;
import it.epicode.bw2.epicenergyservices.entities.Cliente;
import it.epicode.bw2.epicenergyservices.services.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/clienti")
public class ClienteController {
    private final ClienteService clienteService;

    @Autowired
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping
    public Cliente saveCliente(@RequestBody @Validated ClienteDTO payload) {

        return this.clienteService.save(payload);
    }
}
