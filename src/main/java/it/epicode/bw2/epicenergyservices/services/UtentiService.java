package it.epicode.bw2.epicenergyservices.services;

import it.epicode.bw2.epicenergyservices.dto.request.RegisterDTO;
import it.epicode.bw2.epicenergyservices.entities.Ruolo;
import it.epicode.bw2.epicenergyservices.entities.Utente;
import it.epicode.bw2.epicenergyservices.exceptions.BadRequestException;
import it.epicode.bw2.epicenergyservices.exceptions.NotFoundException;
import it.epicode.bw2.epicenergyservices.repositories.RuoliRepository;
import it.epicode.bw2.epicenergyservices.repositories.UtentiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UtentiService {

    private final UtentiRepository utentiRepository;
    private final PasswordEncoder passwordEncoder;
    private final RuoliRepository ruoliRepository;

    @Autowired
    public UtentiService(UtentiRepository utentiRepository, PasswordEncoder passwordEncoder, RuoliRepository ruoliRepository) {
        this.utentiRepository = utentiRepository;
        this.passwordEncoder = passwordEncoder;
        this.ruoliRepository = ruoliRepository;
    }

    public Utente findById(long id) {
        return utentiRepository.findById(id).orElseThrow(() -> new NotFoundException("Utente non trovato"));
    }


    public Utente addUtente(RegisterDTO payload, String ruolo) {
        //verifico che email non esista già:
        Utente found = this.utentiRepository.findByEmail(payload.email());
        if (found != null) throw new BadRequestException("Questa mail è già registrata");

        //verifico che username sia unico:
        boolean usernameExists = this.utentiRepository.existsByUsername(payload.username());
        if (usernameExists) throw new BadRequestException("L'username " + payload.username() + " è già in uso");

        //creo
        Utente newUser = new Utente(payload.username(), payload.email(), passwordEncoder.encode(payload.password()), payload.firstName(), payload.lastName());
        Utente utenteSalvato = this.utentiRepository.save(newUser);
        //TODO: inserire set lista ruoli per mettere USER di default
        Ruolo ruoloFromDB = this.ruoliRepository.findByRuolo(ruolo).orElseThrow(() -> new NotFoundException("Ruolo non trovato"));
        utenteSalvato.getRuoliList().add(ruoloFromDB);
        utentiRepository.save(utenteSalvato);
        //salvo
        return utenteSalvato;
    }

    public Utente findByUsername(String username) {
        return this.utentiRepository.findByUsername(username);
    }

    public Utente findByEmail(String email) {
        return this.utentiRepository.findByEmail(email);
    }

    public Utente addRuoloUtente(Long idUtente, Long idRuolo) {
        Utente utenteFound = this.utentiRepository.findById(idUtente).orElseThrow(() -> new NotFoundException("Utente non trovato"));
        Ruolo ruoloFound = this.ruoliRepository.findById(idRuolo).orElseThrow(() -> new NotFoundException("Ruolo non trovato"));

        utenteFound.getRuoliList().add(ruoloFound);

        return this.utentiRepository.save(utenteFound);

    }

    public boolean existByEmail(String email) {
        return this.utentiRepository.existsByEmail(email);
    }

}
