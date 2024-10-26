package com.example.dogshop.controller;
import com.example.dogshop.entity.Utente;
import com.example.dogshop.entity.UtenteKeycloak;
import com.example.dogshop.service.KeycloakService;
import org.keycloak.representations.account.UserRepresentation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/utente")
public class UtenteController {

    private final KeycloakService keycloakService;

    public UtenteController(KeycloakService keycloakService) {
        this.keycloakService = keycloakService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {
        String token = keycloakService.login(username, password);
        return ResponseEntity.ok(token);
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
