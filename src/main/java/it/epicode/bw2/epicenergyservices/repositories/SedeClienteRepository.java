package it.epicode.bw2.epicenergyservices.repositories;

import it.epicode.bw2.epicenergyservices.entities.SedeCliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SedeClienteRepository extends JpaRepository<SedeCliente, Long> {
    boolean existsByIdCliente(Long idCliente);
}
