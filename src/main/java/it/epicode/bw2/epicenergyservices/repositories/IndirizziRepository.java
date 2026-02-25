package it.epicode.bw2.epicenergyservices.repositories;

import it.epicode.bw2.epicenergyservices.entities.Comune;
import it.epicode.bw2.epicenergyservices.entities.Indirizzo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndirizziRepository extends JpaRepository<Indirizzo, Long> {
    
    /**
     * Conta gli indirizzi associati ad un comune
     * Usato per controllo integrità referenziale prima di eliminare comune
     */
    long countByComuneId(long comuneId);
    boolean existsByViaAndCivicoAndLocalitaAndCapAndComune_Id(
            String via, String civico, String localita, String cap, Long comuneId);}
