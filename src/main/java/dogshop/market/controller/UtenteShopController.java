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

    @PostMapping("/createUser")
    public ResponseEntity<Object> createUser(@RequestBody UtenteShop utenteShop) {
        try {
            ResponseEntity<UtenteShop> savedUtenteShop = keycloakService.createUser(utenteShop);
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


    @PutMapping("/users/{id}")
    public ResponseEntity<UtenteShop> updateUtente(@PathVariable Long id, @RequestBody UtenteShop utenteShopDetails) {
        UtenteShop updatedUtenteShop = utenteShopService.updateUtente(id, utenteShopDetails);
        return updatedUtenteShop != null ? ResponseEntity.ok(updatedUtenteShop) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/users/{username}")
    public ResponseEntity<Void> deleteUtente(@PathVariable String username) {
        boolean isDeleted = utenteShopService.deleteUtente(username);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }



    @GetMapping("/userDetailsDataBase")
    public ResponseEntity<UtenteShop> getUserDetailsDataBase() {
        try{
            return new ResponseEntity<>(utenteShopService.getUserDetailsDataBase(), HttpStatus.OK);
        }catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}