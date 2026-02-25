package it.epicode.bw2.epicenergyservices.repositories;

import it.epicode.bw2.epicenergyservices.entities.Comune;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComuneRepository extends JpaRepository<Comune, Long> {

    Page<Comune> findByNomeComuneContainingIgnoreCase(String q, Pageable pageable);

    Page<Comune> findByProvincia_Id(Long provinciaId, Pageable pageable);

    Page<Comune> findByProvincia_IdAndNomeComuneContainingIgnoreCase(
            Long provinciaId,
            String q,
            Pageable pageable
    );

    boolean existsByProgressivoDelComune(int progressivoDelComune);
    
    /**
     * Conta i comuni associati ad una provincia
     * Usato per controllo integrità referenziale prima di eliminare provincia
     */
    long countByProvinciaId(long provinciaId);
}