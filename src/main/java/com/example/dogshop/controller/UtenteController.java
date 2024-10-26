package com.example.dogshop.controller;
import com.example.dogshop.entity.Utente;
import com.example.dogshop.entity.UtenteKeycloak;
import com.example.dogshop.service.KeycloakService;
import com.example.dogshop.service.UtenteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/utente")

public class UtenteController {

    @Autowired
    private final KeycloakService keycloakService;
    private final UtenteService utenteService;


    public UtenteController(KeycloakService keycloakService, UtenteService utenteService) {
        this.keycloakService = keycloakService;
        this.utenteService = utenteService;
    }

    @PostMapping("/register")
    public ResponseEntity<Utente> registerUser(@RequestBody Utente user) {
        if (utenteService.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        Utente savedUser = utenteService.saveUser(user);
        return ResponseEntity.ok(savedUser);
    }

    @GetMapping("/login")
    public ResponseEntity<Utente> login(Principal principal) {
        String username = principal.getName();
        Utente user = utenteService.findByUsername(username)
                .orElseGet(() -> {
                    Utente newUser = new Utente();
                    newUser.setUsername(username);
                    return utenteService.saveUser(newUser);
                });

        return ResponseEntity.ok(user);
    }


@PostMapping("/create")
    public ResponseEntity<Object> createUser(@RequestBody UtenteKeycloak utente) {
        return keycloakService.createUserInKeycloak(utente);
    }

    @GetMapping("/{username}")
    public ResponseEntity<Utente> getUserByUsername(@PathVariable String username) {
        Utente user = keycloakService.getByUsername(username);
        return ResponseEntity.ok(user);
    }
}
