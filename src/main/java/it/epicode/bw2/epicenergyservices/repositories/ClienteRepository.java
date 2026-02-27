package it.epicode.bw2.epicenergyservices.repositories;

import it.epicode.bw2.epicenergyservices.entities.Cliente;
import it.epicode.bw2.epicenergyservices.entities.TipoAzienda;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository per accedere ai dati di Cliente nel database.
 * Spring genera automaticamente le query dal nome dei metodi.
 * JpaSpecificationExecutor permette di usare Specification per filtri dinamici.
 */
@Repository
public interface ClienteRepository extends
        JpaRepository<Cliente, Long>,
        JpaSpecificationExecutor<Cliente>
{

    // Trova cliente per ID (solo se non cancellato)
    Optional<Cliente> findByIdAndCancellatoFalse(Long id);

    // Trova tutti i clienti attivi (non cancellati), paginati
    Page<Cliente> findByCancellatoFalse(Pageable pageable);

    // Ricerca rapida per nome (case-insensitive) - usato per autocomplete
    List<Cliente> findByRagioneSocialeContainsIgnoreCaseAndCancellatoFalse(String ragioneSociale);

    // Controlla se un email esiste (per evitare duplicati)
    boolean existsByEmail(String email);

    // Controlla se una email contatto esiste
    boolean existsByEmailContatto(String emailContatto);

    // Controlla se una partita IVA esiste (univoca in Italia)
    boolean existsByPartitaIva(String partitaIva);

    // Conta clienti per tipo (usato per statistiche/dashboard)
    long countByTipo(TipoAzienda tipo);

}
