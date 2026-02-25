package it.epicode.bw2.epicenergyservices.controllers;

import it.epicode.bw2.epicenergyservices.dto.request.ClienteDTO;
import it.epicode.bw2.epicenergyservices.dto.request.UpdateContattoDTO;
import it.epicode.bw2.epicenergyservices.dto.response.ClienteResponseDTO;
import it.epicode.bw2.epicenergyservices.entities.Cliente;
import it.epicode.bw2.epicenergyservices.services.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clienti")
public class ClienteController {
    private final ClienteService clienteService;

    @Autowired
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    //CREATE
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/{idComune}")
    public ClienteResponseDTO saveCliente(@RequestBody @Validated ClienteDTO payload, @PathVariable Long idComune) {

        return this.clienteService.save(payload, idComune);
    }

    //GET ALL
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping
    public Page<Cliente> getAll(Pageable pageable) {
        return clienteService.findAll(pageable);
    }

    //GET BY ID
//    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
//    @GetMapping("/{id}")
//    public ClienteResponseDTO getById(@PathVariable Long id) {
//        return clienteService.findClienteById(id);
//    }

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

    // DELETE
//    @PreAuthorize("hasRole('ADMIN')")
//    @DeleteMapping("/{id}")
//    public void deleteCliente(@PathVariable Long id) {
//        clienteService.delete(id);
//    }
}
