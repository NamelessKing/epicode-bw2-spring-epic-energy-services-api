package it.epicode.bw2.epicenergyservices.repositories;

import it.epicode.bw2.epicenergyservices.entities.Comune;
import it.epicode.bw2.epicenergyservices.entities.Indirizzo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndirizziRepository extends JpaRepository<Indirizzo, Long> {
    boolean existsByViaAndCivicoAndLocalitaAndCapAndComune(String via, String civico, String localita, String cap, Comune comune);
}
