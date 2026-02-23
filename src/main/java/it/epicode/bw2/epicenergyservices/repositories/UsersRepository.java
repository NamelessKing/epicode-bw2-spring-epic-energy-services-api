package it.epicode.bw2.epicenergyservices.repositories;

import it.epicode.bw2.epicenergyservices.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UsersRepository extends JpaRepository<User, Long> {
    

    Optional<User> findByEmail(String email);
    

    Optional<User> findByUsername(String username);
    

    boolean existsByEmail(String email);
    

    boolean existsByUsername(String username);
}
