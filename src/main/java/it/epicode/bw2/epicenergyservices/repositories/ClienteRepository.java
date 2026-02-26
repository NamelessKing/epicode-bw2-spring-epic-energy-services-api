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

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long>, JpaSpecificationExecutor<Cliente> {

    Optional<Cliente> findById(Long id);

    Optional<Cliente> findByIdAndCancellatoFalse(Long id);

    Page<Cliente> findByCancellatoFalse(Pageable pageable);

    List<Cliente> findByRagioneSocialeContainsIgnoreCaseAndCancellatoFalse(String ragioneSociale);

    boolean existsByEmail(String email);

    boolean existsByEmailContatto(String emailContatto);

    boolean existsByPartitaIva(String partitaIva);

    long countByTipo(TipoAzienda tipo);

}