package com.example.dogshop.repository;


import com.example.dogshop.entity.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtenteRepository extends JpaRepository<Utente, Long> {
    Optional<Utente> findByUsername(String username);
    Utente findByEmail(String username);
    void deleteByEmail(String email);
}
