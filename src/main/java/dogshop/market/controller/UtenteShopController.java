package dogshop.market.controller;

import dogshop.market.config.AuthenticationService;
import dogshop.market.entity.TokenRequest;
import dogshop.market.entity.UtenteShop;
import dogshop.market.service.KeycloakService;
import dogshop.market.service.UtenteShopService;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/utente")
public class UtenteShopController {

    private final KeycloakService keycloakService;
    private final UtenteShopService utenteShopService;

    @Autowired
    public UtenteShopController(KeycloakService keycloakService, UtenteShopService utenteShopService) {
        this.keycloakService = keycloakService;
        this.utenteShopService = utenteShopService;
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
    public ResponseEntity<Object> createUtenteKeycloak(@RequestBody UtenteShop utenteShop) {
        try {
            UtenteShop savedUtenteShop = keycloakService.createUtenteInKeycloak(utenteShop);
            return ResponseEntity.ok(savedUtenteShop);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @GetMapping("/userDetails")
    public ResponseEntity<Object> getUserDetails() {
        try {
            UtenteShop utenteShop = utenteShopService.getUserDetails();
            return ResponseEntity.ok(utenteShop);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Utente non trovato");
        }
    }
}