package com.example.dogshop.service;

import com.example.dogshop.entity.Utente;
import com.example.dogshop.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UtenteService {

    @Autowired
    private UtenteRepository utenteRepository;

    public Utente findByEmail(String email) {
        return utenteRepository.findByEmail(email);
    }

    public Utente saveUser(Utente user) {
        return utenteRepository.save(user);
    }
}
