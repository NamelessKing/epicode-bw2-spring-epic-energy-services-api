package it.epicode.bw2.epicenergyservices.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class Utente {
    package it.epicode.bw2.epicenergyservices.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

    /**
     * Entity User - rappresenta un utente del sistema
     * Implementa UserDetails di Spring Security per l'autenticazione
     *
     * Campi:
     * - id: identificativo univoco
     * - username: nome utente (unique)
     * - email: email (unique)
     * - passwordHash: password hashata con BCrypt (NON in chiaro!)
     * - firstName: nome dell'utente
     * - lastName: cognome dell'utente
     * - avatarUrl: URL dell'avatar/foto profilo
     * - role: ruolo nel sistema (USER o ADMIN)
     */
    @Entity
    @Table(name = "users", uniqueConstraints = {
            @UniqueConstraint(columnNames = "username"),
            @UniqueConstraint(columnNames = "email")
    })
    @JsonIgnoreProperties({
            "password",
            "authorities",
            "accountNonExpired",
            "accountNonLocked",
            "credentialsNonExpired",
            "enabled"
    })
    public class Utente implements UserDetails {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private long id;

        @Column(nullable = false, unique = true)
        private String username;

        @Column(nullable = false, unique = true)
        private String email;

        @JsonIgnore
        @Column(nullable = false, name = "password_hash")
        private String passwordHash;

        @Column(name = "first_name")
        private String firstName;

        @Column(name = "last_name")
        private String lastName;

        @Column(name = "avatar_url")
        private String avatarUrl;



        // ==================== COSTRUTTORI ====================

        public Utente() {}

        public Utente(String username, String email, String passwordHash) {
            this.username = username;
            this.email = email;
            this.passwordHash = passwordHash;
        }

        // ==================== GETTERS E SETTERS ====================

        public Long getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPasswordHash() {
            return passwordHash;
        }

        public void setPasswordHash(String passwordHash) {
            this.passwordHash = passwordHash;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }

        public Role getRole() {
            return role;
        }

        public void setRole(Role role) {
            this.role = role;
        }

        // ==================== USERDETAILS IMPLEMENTATION ====================
        // Metodi obbligatori di Spring Security

        /**
         * Ritorna le authority (permessi) dell'utente basate sul ruolo
         * Es: USER ha authority "USER", ADMIN ha authority "ADMIN"
         */
        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return List.of(new SimpleGrantedAuthority(this.role.name()));
        }


        @Override
        public String getPassword() {
            return this.passwordHash;
        }


        @Override
        public boolean isAccountNonExpired() {
            return true;
        }


        @Override
        public boolean isAccountNonLocked() {
            return true;
        }


        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }


        @Override
        public boolean isEnabled() {
            return true;
        }
    }
}
