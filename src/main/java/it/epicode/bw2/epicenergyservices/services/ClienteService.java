package it.epicode.bw2.epicenergyservices.services;

import it.epicode.bw2.epicenergyservices.dto.request.ClienteDTO;
import it.epicode.bw2.epicenergyservices.dto.request.UpdateContattoDTO;
import it.epicode.bw2.epicenergyservices.dto.response.ClienteResponseDTO;
import it.epicode.bw2.epicenergyservices.entities.Cliente;
import it.epicode.bw2.epicenergyservices.entities.Comune;
import it.epicode.bw2.epicenergyservices.entities.Indirizzo;
import it.epicode.bw2.epicenergyservices.entities.Provincia;
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
    private final ProvinceService provinceService;
    private final ComuniService comuniService;

    @Autowired
    public ClienteService(IndirizziService indirizziService, ClienteRepository clienteRepository, ProvinceService provinceService, ComuniService comuniService) {
        this.indirizziService = indirizziService;
        this.clienteRepository = clienteRepository;
        this.provinceService = provinceService;
        this.comuniService = comuniService;
    }

    public ClienteResponseDTO toResponseDTO(Cliente cliente) {
        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.isAttivo(),
                cliente.getLogoAziendale(),
                cliente.getRagioneSociale(),
                cliente.getPartitaIva(),
                cliente.getEmail(),
                cliente.getFatturatoAnnuale(),
                cliente.getPec(),
                cliente.getTelefono(),
                cliente.getTipo(),
                cliente.getDataInserimento(),
                cliente.getDataUltimoContatto(),
                cliente.getEmailContatto(),
                cliente.getNomeContatto(),
                cliente.getCognomeContatto(),
                cliente.getTelefonoContatto(),

                cliente.getIndirizzoSedeLegale() != null ? cliente.getIndirizzoSedeLegale().getId() : null,
                cliente.getIndirizzoSedeOperativa() != null ? cliente.getIndirizzoSedeOperativa().getId() : null
        );
    }


    //save
    public ClienteResponseDTO save(ClienteDTO payload) {

        if (clienteRepository.existsByEmail(payload.email()) || clienteRepository.existsByEmail(payload.emailContatto())) {
            throw new BadRequestException("Email cliente o contatto gia in uso");
        }


        if (clienteRepository.existsByPartitaIva(payload.partitaIva())) {
            throw new BadRequestException("Esiste gia un cliente con questa partita iva.");
        }

        Indirizzo indirizzoCliente = indirizziService.save(payload.sedeLegale(), payload.comuneIdSedeLegale());
        Indirizzo indirizzoOperativa = null;
        if (payload.sedeOperativa() != null && payload.comuneIdSedeOperativa() != null) {
            indirizzoOperativa = indirizziService.save(payload.sedeOperativa(), payload.comuneIdSedeOperativa());
        }

        Cliente cliente = new Cliente(
                payload.ragioneSociale(),
                payload.partitaIva(),
                payload.email(),
                payload.fatturatoAnnuale(),
                payload.pec(),
                payload.telefono(),
                payload.logoAziendale(),
                payload.tipo(),
                payload.emailContatto(),
                payload.nomeContatto(),
                payload.cognomeContatto(),
                payload.telefonoContatto(),
                indirizzoCliente,
                indirizzoOperativa
        );


        Cliente saved = clienteRepository.save(cliente);
        return toResponseDTO(saved);
        // TODO: INVIARE EMAIL DI BENVENUTO
    }

    //findById
    public Cliente findClienteById(Long id) {
        Cliente found = clienteRepository.findById(id).orElseThrow(() ->
                new NotFoundException("cliente con id: " + id + " non trovato."));

        return found;

    }

    //findAll
    public Page<Cliente> findAllActive(Pageable pageable) {


        return clienteRepository.findByAttivoTrue(pageable);

    }

    //update cliente
//    public ClienteResponseDTO updateCliente(Long id, ClienteDTO payload) {
//
//        Cliente cliente = findClienteById(id);
//
//        if (!cliente.getEmail().equals(payload.email()) &&
//                (clienteRepository.existsByEmail(payload.email()) || clienteRepository.existsByEmail(payload.emailContatto()))) {
//            throw new BadRequestException("Esiste gia un cliente o un contatto con questa mail");
//        }
//
//        if (!cliente.getPartitaIva().equals(payload.partitaIva()) && clienteRepository.existsByPartitaIva(payload.partitaIva())) {
//            throw new RuntimeException("Esiste già un cliente con questa partita IVA");
//        }
//
//        cliente.setRagioneSociale(payload.ragioneSociale());
//        cliente.setPartitaIva(payload.partitaIva());
//        cliente.setEmail(payload.email());
//        cliente.setFatturatoAnnuale(payload.fatturatoAnnuale());
//        cliente.setPec(payload.pec());
//        cliente.setTelefono(payload.telefono());
//        cliente.setTipo(payload.tipo());
//
//        cliente.setEmailContatto(payload.emailContatto());
//        cliente.setNomeContatto(payload.nomeContatto());
//        cliente.setCognomeContatto(payload.cognomeContatto());
//        cliente.setTelefonoContatto(payload.telefonoContatto());
//
//        Cliente saved = clienteRepository.save(cliente);
//        return toResponseDTO(saved);
//    }

    //update contatto
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

    // imposta lo stato ATTIVO del cliente a false

    public Cliente modificaStato(Long id, boolean isActive) {
        Cliente cliente = findClienteById(id);
        

        cliente.setStato(isActive);

        return clienteRepository.save(cliente);
    }


}