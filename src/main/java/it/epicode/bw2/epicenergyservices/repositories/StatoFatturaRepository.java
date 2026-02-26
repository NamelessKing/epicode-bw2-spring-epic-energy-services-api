package it.epicode.bw2.epicenergyservices.repositories;

import it.epicode.bw2.epicenergyservices.entities.StatoFattura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatoFatturaRepository extends JpaRepository<StatoFattura, Long> {

    StatoFattura findByStato(String nome);

    StatoFattura findById(long id);

    boolean existsByStato(String stato);
}
