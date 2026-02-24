package it.epicode.bw2.epicenergyservices.repositories;


import it.epicode.bw2.epicenergyservices.entities.Ruolo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RuoliRepository extends JpaRepository<Ruolo, Long> {
    Optional<Ruolo> findByRuolo(String ruolo);

    boolean existsByRuolo(String ruolo);
}
