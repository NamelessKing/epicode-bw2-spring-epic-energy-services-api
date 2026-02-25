package it.epicode.bw2.epicenergyservices.repositories;

import it.epicode.bw2.epicenergyservices.entities.Fattura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FatturaRepository extends JpaRepository<Fattura, Long> {

    Optional<Fattura> findById(Long id);

    boolean existsByNumero(Long numero);
}
