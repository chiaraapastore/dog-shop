package dogshop.market.service;

import dogshop.market.config.AuthenticationService;
import dogshop.market.entity.UtenteShop;
import dogshop.market.repository.UtenteShopRepository;
import org.springframework.stereotype.Service;

@Service
public class UtenteShopService {

    private final AuthenticationService authenticationService;
    private final UtenteShopRepository utenteShopRepository;

    public UtenteShopService(AuthenticationService authenticationService, UtenteShopRepository utenteShopRepository) {
        this.authenticationService = authenticationService;
        this.utenteShopRepository = utenteShopRepository;
    }




    public UtenteShop getUserDetails() {
        String username = authenticationService.getUsername();
        if (username == null || username.equals("guest")) {
            throw new RuntimeException("Utente non autenticato");
        }
        System.out.println("Recuperando utente per username: " + username);
        UtenteShop user = utenteShopRepository.findByUsername(username);

        if (user == null) {
            System.err.println("Utente non trovato nel database per username: " + username);
            throw new RuntimeException("Utente non trovato");
        }

        System.out.println("Utente trovato: " + user);
        return user;
    }




}