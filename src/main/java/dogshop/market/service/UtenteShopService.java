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


    public UtenteShop getUserDetailsDataBase() {
        String username = authenticationService.getUsername();
        return utenteShopRepository.findByUsername(username);
    }

    public UtenteShop saveUser(UtenteShop user) {
        if (user.getUsername() == null || user.getEmail() == null) {
            throw new IllegalArgumentException("Username ed email sono obbligatori.");
        }
        return utenteShopRepository.save(user);
    }

    public UtenteShop updateUtente(Long id, UtenteShop utenteShopDetails) {
        return utenteShopRepository.findById(id).map(existingUser -> {
            // Aggiorna i dettagli dell'utente
            existingUser.setFirstName(utenteShopDetails.getFirstName());
            existingUser.setLastName(utenteShopDetails.getLastName());
            existingUser.setEmail(utenteShopDetails.getEmail());
            return utenteShopRepository.save(existingUser);
        }).orElseThrow(() -> new RuntimeException("Utente con ID " + id + " non trovato."));
    }

    public boolean deleteUtente(String username) {
        UtenteShop userToDelete = utenteShopRepository.findByUsername(username);
        if (userToDelete != null) {
            utenteShopRepository.delete(userToDelete);
            return true;
        }
        return false;
    }
}

