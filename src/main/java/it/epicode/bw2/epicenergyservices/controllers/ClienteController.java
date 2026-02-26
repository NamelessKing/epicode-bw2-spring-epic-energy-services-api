package it.epicode.bw2.epicenergyservices.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.epicode.bw2.epicenergyservices.dto.request.ClienteDTO;
import it.epicode.bw2.epicenergyservices.dto.request.UpdateContattoDTO;
import it.epicode.bw2.epicenergyservices.dto.response.ClienteResponseDTO;
import it.epicode.bw2.epicenergyservices.entities.Cliente;
import it.epicode.bw2.epicenergyservices.services.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clienti")
@SecurityRequirement(name = "Bearer Authentication")
public class ClienteController {
    private final ClienteService clienteService;

    @Autowired
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    //CREATE
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ClienteResponseDTO saveCliente(@RequestBody @Validated ClienteDTO payload) {

        return this.clienteService.save(payload);
    }

    //GET ALL
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping
    public Page<Cliente> getAllActive(Pageable pageable) {
        return clienteService.findAllActive(pageable);
    }

    //GET BY ID
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{id}")
    public ClienteResponseDTO getById(@PathVariable Long id) {
        return clienteService.toResponseDTO(clienteService.findClienteById(id));
    }

    //UPDATE COMPLETO
//    @PreAuthorize("hasRole('ADMIN')")
//    @PutMapping("/{id}")
//    public ClienteResponseDTO updateCliente(@PathVariable Long id, @RequestBody @Validated ClienteDTO payload) {
//        return clienteService.updateCliente(id, payload);
//    }

    //UPDATE CONTATTO
//    @PreAuthorize("hasRole('ADMIN')")
//    @PatchMapping("/{id}/contatto")
//    public Cliente updateContatto(@PathVariable Long id, @RequestBody @Validated UpdateContattoDTO payload) {
//        return clienteService.updateContatto(id, payload);
//    }

    // PATCH per modificare lo stato attivo di un cliente
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/disable")
    public ClienteResponseDTO impostaClienteInattivo(@PathVariable Long id) {
        Cliente clienteModificato = clienteService.modificaStato(id, false);
        return clienteService.toResponseDTO(clienteModificato);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/enable")
    public ClienteResponseDTO impostaClienteAttivo(@PathVariable Long id) {
        Cliente clienteModificato = clienteService.modificaStato(id, true);
        return clienteService.toResponseDTO(clienteModificato);
    }
}