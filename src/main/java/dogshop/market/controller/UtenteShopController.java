package dogshop.market.controller;

import dogshop.market.entity.TokenRequest;
import dogshop.market.entity.UtenteShop;
import dogshop.market.service.KeycloakService;
import dogshop.market.service.UtenteShopService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/utente")
public class UtenteShopController {

    private final KeycloakService keycloakService;
    private final UtenteShopService utenteShopService;

    public UtenteShopController(KeycloakService keycloakService, UtenteShopService utenteShopService) {
        this.keycloakService = keycloakService;
        this.utenteShopService = utenteShopService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody UtenteShop utenteShop) {
        Map<String, Object> response = new HashMap<>();
        try {
            UtenteShop savedUser = keycloakService.createUtenteInKeycloak(utenteShop);
            response.put("message", "Utente registrato con successo");
            response.put("user", savedUser);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Errore durante la registrazione");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody TokenRequest tokenRequest) {
        Map<String, Object> response = new HashMap<>();
        try {
            String token = keycloakService.login(tokenRequest.getUsername(), tokenRequest.getPassword());
            response.put("message", "Login effettuato con successo");
            response.put("token", token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Credenziali non valide");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @GetMapping("/userDetails")
    public ResponseEntity<Object> getUserDetails() {
        try {
            UtenteShop utenteShop = utenteShopService.getUserDetails();
            return ResponseEntity.ok(utenteShop);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utente non trovato");
        }
    }
}
