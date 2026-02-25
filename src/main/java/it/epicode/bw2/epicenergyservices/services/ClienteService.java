package it.epicode.bw2.epicenergyservices.services;

import it.epicode.bw2.epicenergyservices.dto.request.ClienteDTO;
import it.epicode.bw2.epicenergyservices.dto.request.UpdateContattoDTO;
import it.epicode.bw2.epicenergyservices.entities.Cliente;
import it.epicode.bw2.epicenergyservices.entities.Indirizzo;
import it.epicode.bw2.epicenergyservices.exceptions.BadRequestException;
import it.epicode.bw2.epicenergyservices.exceptions.NotFoundException;
import it.epicode.bw2.epicenergyservices.repositories.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ClienteService {

    private final IndirizziService indirizziService;
    private final ClienteRepository clienteRepository;

    @Autowired
    public ClienteService(IndirizziService indirizziService, ClienteRepository clienteRepository) {
        this.indirizziService = indirizziService;
        this.clienteRepository = clienteRepository;
    }

    //save
    public Cliente save(ClienteDTO payload) {
        if (clienteRepository.existsByEmail(payload.email()) || clienteRepository.existsByEmail(payload.emailContatto())) {
            throw new BadRequestException("Email cliente o contatto gia in uso");
        }
        ;

        if (clienteRepository.existsByPartitaIva(payload.partitaIva())) {
            throw new BadRequestException("Esiste gia un cliente con questa partita iva.");
        }

        Cliente cliente = new Cliente();
        cliente.setRagioneSociale(payload.ragioneSociale());
        cliente.setPartitaIva(payload.partitaIva());
        cliente.setEmail(payload.email());
        cliente.setFatturatoAnnuale(payload.fatturatoAnnuale());
        cliente.setPec(payload.pec());
        cliente.setTelefono(payload.telefono());
        cliente.setTipo(payload.tipo());
        cliente.setDataInserimento(LocalDate.now());

        cliente.setEmailContatto(payload.emailContatto());
        cliente.setNomeContatto(payload.nomeContatto());
        cliente.setCognomeContatto(payload.cognomeContatto());
        cliente.setTelefonoContatto(payload.telefonoContatto());

        if (payload.idSedeLegale() != null) {
            Indirizzo sedeLegale = indirizziService.findById(payload.idSedeLegale());
            cliente.setIndirizzoSedeLegale(sedeLegale);
        }

        if (payload.idSedeOperativa() != null) {
            Indirizzo sedeOperativa = indirizziService.findById(payload.idSedeOperativa());
            cliente.setIndirizzoSedeOperativa(sedeOperativa);
        }

        return clienteRepository.save(cliente);
        // TODO: INVIARE EMAIL DI BENVENUTO
    }

    //findById
    public Cliente findClienteById(Long id) {

        return clienteRepository.findById(id).orElseThrow(() ->
                new NotFoundException("cliente con id: " + id + " non trovato."));


    }

    //findAll
    public Page<Cliente> findAll(Pageable pageable) {
        return clienteRepository.findAll(pageable);

    }

    //update cliente
    public Cliente updateCliente(Long id, ClienteDTO payload) {

        Cliente cliente = findClienteById(id);

        if (!cliente.getEmail().equals(payload.email()) &&
                (clienteRepository.existsByEmail(payload.email()) || clienteRepository.existsByEmail(payload.emailContatto()))) {
            throw new BadRequestException("Esiste gia un cliente o un contatto con questa mail");
        }

        if (!cliente.getPartitaIva().equals(payload.partitaIva()) && clienteRepository.existsByPartitaIva(payload.partitaIva())) {
            throw new RuntimeException("Esiste già un cliente con questa partita IVA");
        }

        cliente.setRagioneSociale(payload.ragioneSociale());
        cliente.setPartitaIva(payload.partitaIva());
        cliente.setEmail(payload.email());
        cliente.setFatturatoAnnuale(payload.fatturatoAnnuale());
        cliente.setPec(payload.pec());
        cliente.setTelefono(payload.telefono());
        cliente.setTipo(payload.tipo());

        cliente.setEmailContatto(payload.emailContatto());
        cliente.setNomeContatto(payload.nomeContatto());
        cliente.setCognomeContatto(payload.cognomeContatto());
        cliente.setTelefonoContatto(payload.telefonoContatto());

        return clienteRepository.save(cliente);
    }

    //update cliente
    public Cliente updateContatto(Long id, UpdateContattoDTO payload) {

        Cliente cliente = findClienteById(id);

        if (!cliente.getEmailContatto().equals(payload.emailContatto()) && clienteRepository.existsByEmail(payload.emailContatto())) {
            throw new BadRequestException("Esiste gia un contatto con questa mail");
        }

        cliente.setEmailContatto(payload.emailContatto());
        cliente.setNomeContatto(payload.nomeContatto());
        cliente.setCognomeContatto(payload.cognomeContatto());
        cliente.setTelefonoContatto(payload.telefonoContatto());

        return clienteRepository.save(cliente);
    }

    // elimina cliente
    public void delete(Long id) {
        Cliente cliente = findClienteById(id);
        clienteRepository.delete(cliente);
    }


}
