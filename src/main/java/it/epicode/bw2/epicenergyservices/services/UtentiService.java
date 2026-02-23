package it.epicode.bw2.epicenergyservices.services;

import it.epicode.bw2.epicenergyservices.dto.request.RegisterDTO;
import it.epicode.bw2.epicenergyservices.entities.Utente;
import it.epicode.bw2.epicenergyservices.exceptions.BadRequestException;
import it.epicode.bw2.epicenergyservices.exceptions.NotFoundException;
import it.epicode.bw2.epicenergyservices.repositories.UtentiRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UtentiService {

    private final UtentiRepository utentiRepository;
    private final PasswordEncoder passwordEncoder;

    public UtentiService(UtentiRepository utentiRepository, PasswordEncoder passwordEncoder) {
        this.utentiRepository = utentiRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Utente findById(long id) {
        return utentiRepository.findById(id).orElseThrow(() -> new NotFoundException("Utente non trovato"));
    }


    public Utente addUtente(RegisterDTO payload) {
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

        //salvo
        return utenteSalvato;
    }


    public Utente findByUsername(String username) {
        return this.utentiRepository.findByUsername(username);
    }

    public Utente findByEmail(String email) {
        return this.utentiRepository.findByEmail(email);
    }


}
