package it.epicode.bw2.epicenergyservices.services;

import it.epicode.bw2.epicenergyservices.dto.request.RegisterDTO;
import it.epicode.bw2.epicenergyservices.entities.Role;
import it.epicode.bw2.epicenergyservices.entities.User;
import it.epicode.bw2.epicenergyservices.exceptions.BadRequestException;
import it.epicode.bw2.epicenergyservices.exceptions.NotFoundException;
import it.epicode.bw2.epicenergyservices.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * - Registrazione di nuovi utenti
 * - Ricerca utenti (per ID, email)
 * - Validazione durante registrazione (unicità email/username)
 * - Hashing password con BCrypt
 * 
 */
@Service
public class UsersService {
    
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public UsersService(UsersRepository usersRepository, 
                       PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * Registra un nuovo utente nel sistema
     * - Email non deve essere già in uso
     * - Username non deve essere già in uso
     * - Password viene hashata con BCrypt (mai in chiaro)
     * - Role deve essere USER o ADMIN
     */
    @Transactional
    public User save(RegisterDTO payload) {
        // 1. Verifica che email non sia già in uso
        if (usersRepository.existsByEmail(payload.email())) {
            throw new BadRequestException("Email già in uso!");
        }
        
        // 2. Verifica che username non sia già in uso
        if (usersRepository.existsByUsername(payload.username())) {
            throw new BadRequestException("Username già in uso!");
        }
        
        // 3. Hasha la password con BCrypt
        // NON memorizzare mai password in chiaro!
        String hashedPassword = passwordEncoder.encode(payload.password());
        
        // 4. Determina il ruolo
        // Se payload.role() è "ADMIN" → Role.ADMIN, altrimenti Role.USER
        Role role = "ADMIN".equalsIgnoreCase(payload.role()) 
            ? Role.ADMIN 
            : Role.USER;
        
        // 5. Crea nuova entity User
        User newUser = new User(
            payload.username(),
            payload.email(),
            hashedPassword,
            role
        );
        
        // 6. Imposta campi opzionali (primo nome, cognome, avatar)
        if (payload.firstName() != null && !payload.firstName().isBlank()) {
            newUser.setFirstName(payload.firstName());
        }
        if (payload.lastName() != null && !payload.lastName().isBlank()) {
            newUser.setLastName(payload.lastName());
        }
        if (payload.avatarUrl() != null && !payload.avatarUrl().isBlank()) {
            newUser.setAvatarUrl(payload.avatarUrl());
        }
        
        // 7. Salva nel database
        User saved = usersRepository.save(newUser);
        System.out.println("✅ User registrato: " + saved.getId() + " (" + saved.getEmail() + ")");
        
        return saved;
    }
    
    /**
     * Cerca un utente per ID
     */
    public User findById(Long id) {
        return usersRepository.findById(id)
            .orElseThrow(() -> new NotFoundException(id));
    }
    
    /**
     * Cerca un utente per email
     * Usato principalmente in AuthService.login()
     */
    public User findByEmail(String email) {
        return usersRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("User con email " + email + " non trovato!"));
    }
}
