package it.epicode.bw2.epicenergyservices.services;

import it.epicode.bw2.epicenergyservices.dto.request.RegisterDTO;
import it.epicode.bw2.epicenergyservices.entities.Ruolo;
import it.epicode.bw2.epicenergyservices.entities.Utente;
import it.epicode.bw2.epicenergyservices.exceptions.BadRequestException;
import it.epicode.bw2.epicenergyservices.exceptions.NotFoundException;
import it.epicode.bw2.epicenergyservices.repositories.RuoliRepository;
import it.epicode.bw2.epicenergyservices.repositories.UtentiRepository;
import it.epicode.bw2.epicenergyservices.tools.EmailSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UtentiService {

    private final UtentiRepository utentiRepository;
    private final PasswordEncoder passwordEncoder;
    private final RuoliRepository ruoliRepository;


    @Autowired
    public UtentiService(UtentiRepository utentiRepository, PasswordEncoder passwordEncoder, RuoliRepository ruoliRepository, EmailSender mailgun) {
        this.utentiRepository = utentiRepository;
        this.passwordEncoder = passwordEncoder;
        this.ruoliRepository = ruoliRepository;
    }

    public Utente findById(long id) {
        return utentiRepository.findById(id).orElseThrow(() -> new NotFoundException("Utente non trovato"));
    }


    @Transactional
    public Utente addUtente(RegisterDTO payload, String ruolo) {
        // 1 Verifica che email non esista già
        Utente found = this.utentiRepository.findByEmail(payload.email());
        if (found != null) throw new BadRequestException("Questa mail è già registrata");

        // 2 Verifica che username sia unico
        boolean usernameExists = this.utentiRepository.existsByUsername(payload.username());
        if (usernameExists) throw new BadRequestException("L'username " + payload.username() + " è già in uso");

        // 3 Crea nuovo utente
        Utente newUser = new Utente(
                payload.username(),
                payload.email(),
                passwordEncoder.encode(payload.password()),
                payload.firstName(),
                payload.lastName());

        // 4 Salva l'utente
        Utente utenteSalvato = this.utentiRepository.save(newUser);

        // 5 Carica il ruolo dal DB
        Ruolo ruoloFromDB = this.ruoliRepository.findByRuolo(ruolo)
                .orElseThrow(() -> new NotFoundException("Ruolo '" + ruolo + "' non trovato nel database"));

        // 6 Aggiungi il ruolo all'utente
        utenteSalvato.getRuoliList().add(ruoloFromDB);

        // 7 Salva l'utente con il ruolo assegnato
        this.utentiRepository.save(utenteSalvato);

        System.out.println("Utente registrato: " + utenteSalvato.getUsername() + " con ruolo: " + ruolo);

        return utenteSalvato;
    }

    public Utente findByUsername(String username) {
        return this.utentiRepository.findByUsername(username);
    }

    public Utente findByEmail(String email) {
        return this.utentiRepository.findByEmail(email);
    }

    /**
     * Assegna un ruolo ad un utente (utile per modificare i ruoli dopo la registrazione)
     */
    @Transactional
    public Utente addRuoloUtente(Long idUtente, Long idRuolo) {
        Utente utenteFound = this.utentiRepository.findById(idUtente)
                .orElseThrow(() -> new NotFoundException("Utente non trovato"));

        Ruolo ruoloFound = this.ruoliRepository.findById(idRuolo)
                .orElseThrow(() -> new NotFoundException("Ruolo non trovato"));

        // Controlla che l'utente non abbia già questo ruolo
        boolean alreadyHasRole = utenteFound.getRuoliList().stream()
                .anyMatch(r -> r.getId() == idRuolo);

        if (alreadyHasRole) {
            throw new BadRequestException("Utente ha già questo ruolo");
        }

        utenteFound.getRuoliList().add(ruoloFound);

        return this.utentiRepository.save(utenteFound);
    }

    public boolean existByEmail(String email) {
        return this.utentiRepository.existsByEmail(email);
    }
}