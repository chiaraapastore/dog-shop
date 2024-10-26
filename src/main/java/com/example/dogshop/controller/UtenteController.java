package com.example.dogshop.controller;
import com.example.dogshop.config.AuthenticationService;
import com.example.dogshop.entity.TokenRequest;
import com.example.dogshop.entity.Utente;
import com.example.dogshop.entity.UtenteKeycloak;
import com.example.dogshop.service.KeycloakService;
import com.example.dogshop.service.UtenteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utente")

public class UtenteController {

    @Autowired
    private final KeycloakService keycloakService;
    private final UtenteService utenteService;
    private final AuthenticationService authenticationService;


    public UtenteController(KeycloakService keycloakService, UtenteService utenteService, AuthenticationService authenticationService) {
        this.keycloakService = keycloakService;
        this.utenteService = utenteService;
        this.authenticationService = authenticationService;
    }


    @GetMapping("/utenti")
    public List<Utente> getAllUtenti() {
        return utenteService.getAllUtenti();
    }

    @PostMapping("/register")
    public ResponseEntity<Utente> registerUser(@RequestBody Utente user) {
        if (utenteService.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        Utente savedUser = utenteService.saveUser(user);
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody TokenRequest tokenRequest) {
        try {
            String token = keycloakService.login(tokenRequest.getUsername(), tokenRequest.getPassword());
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenziali non valide");
        }
    }


    @PostMapping("/create/users/keycloak")
    public ResponseEntity<Utente> createUtenteKeycloak(@RequestBody Utente utente) {
        try {
            UtenteKeycloak utenteKeycloak = new UtenteKeycloak();
            utenteKeycloak.setUsername(utente.getUsername());
            utenteKeycloak.setEmail(utente.getEmail());
            utenteKeycloak.setRole(utente.getRole());

            ResponseEntity<Object> response = keycloakService.createUserInKeycloak(utenteKeycloak);

            if (response.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.ok(utenteService.saveUser(utente));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/user-info")
    public ResponseEntity<String> getUserInfo() {
        String userId = authenticationService.getUserId();
        return ResponseEntity.ok("User ID: " + userId);
    }



    @PutMapping("/utenti/{id}")
    public ResponseEntity<Utente> updateUtente(@PathVariable Long id, @RequestBody Utente utenteDetails) {
        Utente updatedUtente = utenteService.updateUtente(id, utenteDetails);
        return updatedUtente != null ? ResponseEntity.ok(updatedUtente) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/utenti/{email}")
    public ResponseEntity<Void> deleteUtente(@PathVariable String email) {
        boolean isDeleted = utenteService.deleteUtente(email);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/{username}")
    public ResponseEntity<Utente> getUserByUsername(@PathVariable String username) {
        Utente user = keycloakService.getByUsername(username);
        return ResponseEntity.ok(user);
    }
}
