package it.epicode.bw2.epicenergyservices.repositories;

import it.epicode.bw2.epicenergyservices.entities.Fattura;
import it.epicode.bw2.epicenergyservices.entities.StatoFattura;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FatturaRepository extends JpaRepository<Fattura, Long> {

    Fattura findFatturaById(Long id);

    boolean existsByNumero(Long numero);

    //filtrare per Cliente
    Page<Fattura> findByClienteId(Long clienteId, Pageable pageable);

    //filtrare per stato
    @Query("SELECT f FROM Fattura f WHERE f.statoFattura.stato = :stato")
    Page<Fattura> findByStatoStato(String stato, Pageable pageable);

    //filtrare per data
    Page<Fattura> findByData(LocalDate data, Pageable pageable);

    //filtrare per anno
    @Query("SELECT f FROM Fattura f WHERE YEAR(f.data) = :anno")
    Page<Fattura> findByAnno(int anno, Pageable pageable);

    //filtrare per range di importi
    Page<Fattura> findByImportoBetween(BigDecimal min, BigDecimal max, Pageable pageable);

}
