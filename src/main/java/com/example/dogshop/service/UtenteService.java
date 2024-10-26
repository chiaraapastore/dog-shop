package com.example.dogshop.service;

import com.example.dogshop.entity.Utente;
import com.example.dogshop.repository.UtenteRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Data
@Service
public class UtenteService {

    private final UtenteRepository utenteRepository;

    @Autowired
    public UtenteService(UtenteRepository utenteRepository) {
        this.utenteRepository = utenteRepository;
    }

    public String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        throw new RuntimeException("User not authenticated");
    }

    public Optional<Utente> findByUsername(String username) {
        return utenteRepository.findByUsername(username);
    }

    public Utente saveUser(Utente user) {
        return utenteRepository.save(user);
    }

    public Utente findByEmail(String email) {
        return utenteRepository.findByEmail(email);
    }

    public List<Utente> getAllUtenti() {
        return utenteRepository.findAll();
    }

    public Utente updateUtente(Long id, Utente utenteDetails) {
        Optional<Utente> optionalUtente = utenteRepository.findById(id);
        if (optionalUtente.isPresent()) {
            Utente utente = optionalUtente.get();
            utente.setFirstName(utenteDetails.getFirstName());
            return utenteRepository.save(utente);
        }
        return null;
    }
    public boolean deleteUtente(String email) {
        Utente optionalUtente = utenteRepository.findByEmail(email);
        if (optionalUtente != null) {
            utenteRepository.deleteByEmail(email);
            return true;
        }
        return false;
    }
}
