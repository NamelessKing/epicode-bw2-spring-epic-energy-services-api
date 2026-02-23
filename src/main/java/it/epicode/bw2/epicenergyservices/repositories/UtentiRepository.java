package it.epicode.bw2.epicenergyservices.repositories;

import it.epicode.bw2.epicenergyservices.entities.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UtentiRepository extends JpaRepository<Utente, Long> {


    Utente findByEmail(String email);


    Utente findByUsername(String username);


    boolean existsByEmail(String email);


    boolean existsByUsername(String username);
}
