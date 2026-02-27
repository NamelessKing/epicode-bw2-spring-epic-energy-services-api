package it.epicode.bw2.epicenergyservices.services;

import it.epicode.bw2.epicenergyservices.dto.request.ClienteCreateDTO;
import it.epicode.bw2.epicenergyservices.dto.request.ClienteUpdateDTO;
import it.epicode.bw2.epicenergyservices.dto.request.UpdateContattoDTO;
import it.epicode.bw2.epicenergyservices.dto.request.UpdateLogoDTO;
import it.epicode.bw2.epicenergyservices.dto.request.EmailDTO;
import it.epicode.bw2.epicenergyservices.dto.response.ClienteResponseDTO;
import it.epicode.bw2.epicenergyservices.dto.response.ClienteListItemDTO;
import it.epicode.bw2.epicenergyservices.dto.response.ClienteSearchDTO;
import it.epicode.bw2.epicenergyservices.entities.Cliente;
import it.epicode.bw2.epicenergyservices.entities.Indirizzo;
import it.epicode.bw2.epicenergyservices.entities.TipoAzienda;
import it.epicode.bw2.epicenergyservices.exceptions.BadRequestException;
import it.epicode.bw2.epicenergyservices.exceptions.NotFoundException;
import it.epicode.bw2.epicenergyservices.repositories.ClienteRepository;
import lombok.extern.slf4j.Slf4j;
import it.epicode.bw2.epicenergyservices.specifications.ClienteSpecification;
import it.epicode.bw2.epicenergyservices.tools.EmailSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class ClienteService {

    private final IndirizziService indirizziService;
    private final ClienteRepository clienteRepository;
    private final EmailSender mailgun;


    @Autowired
    public ClienteService(IndirizziService indirizziService, ClienteRepository clienteRepository,  EmailSender mailgun) {
        this.indirizziService = indirizziService;
        this.clienteRepository = clienteRepository;

        this.mailgun = mailgun;
    }

    public ClienteResponseDTO toResponseDTO(Cliente cliente) {
        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.getCancellato(),
                cliente.getRagioneSociale(),
                cliente.getPartitaIva(),
                cliente.getEmail(),
                cliente.getFatturatoAnnuale(),
                cliente.getPec(),
                cliente.getTelefono(),
                cliente.getLogoAziendale(),
                cliente.getTipo(),
                cliente.getDataInserimento(),
                cliente.getDataUltimoContatto(),
                cliente.getEmailContatto(),
                cliente.getNomeContatto(),
                cliente.getCognomeContatto(),
                cliente.getTelefonoContatto(),
                cliente.getIndirizzoSedeLegale() != null ?
                        new it.epicode.bw2.epicenergyservices.dto.response.IndirizzoResponseDTO(
                                cliente.getIndirizzoSedeLegale().getId(),
                                cliente.getIndirizzoSedeLegale().getVia(),
                                cliente.getIndirizzoSedeLegale().getCivico(),
                                cliente.getIndirizzoSedeLegale().getLocalita(),
                                cliente.getIndirizzoSedeLegale().getCap(),
                                new it.epicode.bw2.epicenergyservices.dto.response.IndirizzoResponseDTO.ComuneEmbeddedDTO(
                                        cliente.getIndirizzoSedeLegale().getComune().getId(),
                                        cliente.getIndirizzoSedeLegale().getComune().getNomeComune()
                                )
                        ) : null,
                cliente.getIndirizzoSedeOperativa() != null ?
                        new it.epicode.bw2.epicenergyservices.dto.response.IndirizzoResponseDTO(
                                cliente.getIndirizzoSedeOperativa().getId(),
                                cliente.getIndirizzoSedeOperativa().getVia(),
                                cliente.getIndirizzoSedeOperativa().getCivico(),
                                cliente.getIndirizzoSedeOperativa().getLocalita(),
                                cliente.getIndirizzoSedeOperativa().getCap(),
                                new it.epicode.bw2.epicenergyservices.dto.response.IndirizzoResponseDTO.ComuneEmbeddedDTO(
                                        cliente.getIndirizzoSedeOperativa().getComune().getId(),
                                        cliente.getIndirizzoSedeOperativa().getComune().getNomeComune()
                                )
                        ) : null
        );
    }

    public ClienteListItemDTO toListItemDTO(Cliente cliente) {
        String nomeProvincia = cliente.getIndirizzoSedeLegale() != null &&
                cliente.getIndirizzoSedeLegale().getComune() != null &&
                cliente.getIndirizzoSedeLegale().getComune().getProvincia() != null ?
                cliente.getIndirizzoSedeLegale().getComune().getProvincia().getSigla() : "";

        return new ClienteListItemDTO(
                cliente.getId(),
                cliente.getRagioneSociale(),
                cliente.getPartitaIva(),
                cliente.getEmail(),
                cliente.getFatturatoAnnuale(),
                cliente.getTipo(),
                cliente.getDataInserimento(),
                cliente.getDataUltimoContatto(),
                nomeProvincia
        );
    }

    public ClienteSearchDTO toSearchDTO(Cliente cliente) {
        return new ClienteSearchDTO(
                cliente.getId(),
                cliente.getRagioneSociale(),
                cliente.getPartitaIva(),
                cliente.getEmail()
        );
    }


    //save (CREATE)
    public ClienteResponseDTO save(ClienteCreateDTO payload) {

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
        
        // ⚠️ EMAIL DISABILITATA - Presentazione in corso
        // TODO: Riabilitare dopo sistemazione dipendenza Mailgun
        // try {
        //     this.mailgun.sendRegistration(saved);
        //     log.info("✅ Email di registrazione inviata a: {}", saved.getEmail());
        // } catch (Exception ex) {
        //     log.error("❌ Errore nell'invio email: {}", ex.getMessage());
        // }
        
        log.info("✅ Cliente salvato con ID: {} - Email DISABILITATA", saved.getId());
        
        return toResponseDTO(saved);
    }

    //findById (solo clienti attivi)
    public Cliente findClienteById(Long id) {
        return clienteRepository.findByIdAndCancellatoFalse(id).orElseThrow(() ->
                new NotFoundException("cliente con id: " + id + " non trovato."));
    }

    private Cliente findClienteByIdIncludingDeleted(Long id) {
        return clienteRepository.findById(id).orElseThrow(() ->
                new NotFoundException("cliente con id: " + id + " non trovato."));
    }

    //findAll (paginato, clienti attivi)
    public Page<ClienteListItemDTO> findAllActive(Pageable pageable) {
        Page<Cliente> clienti = clienteRepository.findByCancellatoFalse(pageable);
        List<ClienteListItemDTO> dtos = clienti.getContent().stream()
                .map(this::toListItemDTO)
                .toList();
        return new PageImpl<>(dtos, pageable, clienti.getTotalElements());
    }

    // Ricerca clienti con filtri dinamici
    public Page<ClienteListItemDTO> findAllActiveWithFilters(
            Pageable pageable,
            String nome,
            Double fatturatoMin,
            Double fatturatoMax,
            LocalDate dataInserimentoDa,
            LocalDate dataInserimentoA,
            LocalDate dataContattoDa,
            LocalDate dataContattoA,
            Long provinciaId,
            TipoAzienda tipoAzienda
    ) {
        // Crea la Specification con tutti i filtri
        Specification<Cliente> spec = ClienteSpecification.withFilters(
                nome, fatturatoMin, fatturatoMax,
                dataInserimentoDa, dataInserimentoA,
                dataContattoDa, dataContattoA,
                provinciaId, tipoAzienda
        );

        // Esegue la query con Specification + paginazione
        Page<Cliente> clienti = clienteRepository.findAll(spec, pageable);

        // Converte Entity in DTO per ridurre il payload
        List<ClienteListItemDTO> dtos = clienti.getContent().stream()
                .map(this::toListItemDTO)
                .toList();

        return new PageImpl<>(dtos, pageable, clienti.getTotalElements());
    }

    //searchClienti (ricerca rapida)
    public List<ClienteSearchDTO> searchClienti(String query) {
        List<Cliente> clienti = clienteRepository.findByRagioneSocialeContainsIgnoreCaseAndCancellatoFalse(query);
        return clienti.stream()
                .map(this::toSearchDTO)
                .toList();
    }

    //update (UPDATE completo)
    public ClienteResponseDTO updateCliente(Long id, ClienteUpdateDTO payload) {
        Cliente cliente = findClienteById(id);

        if (payload.email() != null && !cliente.getEmail().equals(payload.email()) &&
                clienteRepository.existsByEmail(payload.email())) {
            throw new BadRequestException("Email già in uso");
        }

        if (payload.ragioneSociale() != null) cliente.setRagioneSociale(payload.ragioneSociale());
        if (payload.email() != null) cliente.setEmail(payload.email());
        if (payload.fatturatoAnnuale() != null) cliente.setFatturatoAnnuale(payload.fatturatoAnnuale());
        if (payload.pec() != null) cliente.setPec(payload.pec());
        if (payload.telefono() != null) cliente.setTelefono(payload.telefono());
        if (payload.logoAziendale() != null) cliente.setLogoAziendale(payload.logoAziendale());
        if (payload.tipo() != null) cliente.setTipo(payload.tipo());

        // Update indirizzi se forniti
        if (payload.sedeLegale() != null && payload.comuneIdSedeLegale() != null) {
            Indirizzo sedeLegale = indirizziService.save(payload.sedeLegale(), payload.comuneIdSedeLegale());
            cliente.setIndirizzoSedeLegale(sedeLegale);
        }
        if (payload.sedeOperativa() != null && payload.comuneIdSedeOperativa() != null) {
            Indirizzo sedeOperativa = indirizziService.save(payload.sedeOperativa(), payload.comuneIdSedeOperativa());
            cliente.setIndirizzoSedeOperativa(sedeOperativa);
        }

        Cliente saved = clienteRepository.save(cliente);
        return toResponseDTO(saved);
    }

    //updateLogo
    public ClienteResponseDTO updateLogo(Long id, UpdateLogoDTO payload) {
        Cliente cliente = findClienteById(id);
        cliente.setLogoAziendale(payload.logoAziendale());
        Cliente saved = clienteRepository.save(cliente);
        return toResponseDTO(saved);
    }

    //updateUltimoContatto
    public ClienteResponseDTO updateUltimoContatto(Long id) {
        Cliente cliente = findClienteById(id);
        cliente.setDataUltimoContatto(LocalDate.now());
        Cliente saved = clienteRepository.save(cliente);
        return toResponseDTO(saved);
    }

    //update contatto
    public ClienteResponseDTO updateContatto(Long id, UpdateContattoDTO payload) {
        Cliente cliente = findClienteById(id);

        if (!cliente.getEmailContatto().equals(payload.emailContatto()) &&
                clienteRepository.existsByEmailContatto(payload.emailContatto())) {
            throw new BadRequestException("Esiste gia un contatto con questa mail");
        }

        cliente.setEmailContatto(payload.emailContatto());
        cliente.setNomeContatto(payload.nomeContatto());
        cliente.setCognomeContatto(payload.cognomeContatto());
        cliente.setTelefonoContatto(payload.telefonoContatto());

        Cliente saved = clienteRepository.save(cliente);
        return toResponseDTO(saved);
    }

    //delete (SOFT DELETE)
    public void deleteCliente(Long id) {
        Cliente cliente = findClienteById(id);
        cliente.setCancellato(true);
        clienteRepository.save(cliente);
    }

    //restore (RIATTIVA CLIENTE CANCELLATO)
    public ClienteResponseDTO restoreCliente(Long id) {
        Cliente cliente = findClienteByIdIncludingDeleted(id);
        cliente.setCancellato(false);
        Cliente saved = clienteRepository.save(cliente);
        return toResponseDTO(saved);
    }

    //sendEmail (INVIA EMAIL AL CONTATTO CLIENTE)
    public void sendEmailToCliente(Long clienteId, EmailDTO emailDTO) {
        Cliente cliente = findClienteById(clienteId);
        
        // Valida email contatto
        if (cliente.getEmailContatto() == null || cliente.getEmailContatto().isEmpty()) {
            throw new BadRequestException("Cliente non ha email di contatto configurata");
        }
        
        // ⚠️ EMAIL DISABILITATA - Presentazione in corso
        // try {
        //     mailgun.sendCustomEmail(
        //         cliente.getEmailContatto(),
        //         emailDTO.soggetto(),
        //         emailDTO.corpo()
        //     );
        //     log.info("✅ Email inviata con successo a: {} - Soggetto: {}", 
        //             cliente.getEmailContatto(), emailDTO.soggetto());
        // } catch (Exception ex) {
        //     log.error("Errore nell'invio email a: {} - Error: {}",
        //             cliente.getEmailContatto(), ex.getMessage(), ex);
        //     throw new BadRequestException("Errore nell'invio dell'email: " + ex.getMessage());
        // }
        
        log.info("⚠️ Email DISABILITATA - Destinatario: {} - Soggetto: {} [SIMULAZIONE]", 
                cliente.getEmailContatto(), emailDTO.soggetto());
    }
}