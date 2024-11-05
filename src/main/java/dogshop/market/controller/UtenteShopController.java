package dogshop.market.controller;

import dogshop.market.config.AuthenticationService;
import dogshop.market.entity.TokenRequest;
import dogshop.market.entity.UtenteShop;
import dogshop.market.service.KeycloakService;
import dogshop.market.service.UtenteShopService;
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
    private final AuthenticationService authenticationService;

    @Autowired
    public UtenteShopController(KeycloakService keycloakService, UtenteShopService utenteShopService, AuthenticationService authenticationService) {
        this.keycloakService = keycloakService;
        this.utenteShopService = utenteShopService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<UtenteShop> registerUser(@RequestBody UtenteShop user) {
        UtenteShop savedUser = utenteShopService.saveUser(user);
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
    public ResponseEntity<Object> createUtenteKeycloak(@RequestBody UtenteShop utenteShop) {
        try {
            UtenteShop savedUtenteShop = keycloakService.createUtenteInKeycloak(utenteShop);
            return ResponseEntity.ok(savedUtenteShop);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/user-info")
    public ResponseEntity<String> getUserInfo() {
        String userId = authenticationService.getUserId();
        return ResponseEntity.ok("User ID: " + userId);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UtenteShop>> getAllUtenti() {
        List<UtenteShop> utenti = utenteShopService.getAllUtenti();
        return ResponseEntity.ok(utenti);
    }


    @PutMapping("/utenti/{id}")
    public ResponseEntity<UtenteShop> updateUtente(@PathVariable Long id, @RequestBody UtenteShop utenteShopDetails) {
        UtenteShop updatedUtenteShop = utenteShopService.updateUtente(id, utenteShopDetails);
        return updatedUtenteShop != null ? ResponseEntity.ok(updatedUtenteShop) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/utenti/{email}")
    public ResponseEntity<Void> deleteUtente(@PathVariable String email) {
        boolean isDeleted = utenteShopService.deleteUtente(email);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

}
