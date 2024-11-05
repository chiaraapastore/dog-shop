package dogshop.market.service;

import dogshop.market.entity.UtenteShop;
import dogshop.market.repository.UtenteShopRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Data
@Service
public class UtenteShopService {

    private final UtenteShopRepository utenteShopRepository;

    @Autowired
    public UtenteShopService(UtenteShopRepository utenteShopRepository) {
        this.utenteShopRepository = utenteShopRepository;
    }

    public String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        throw new RuntimeException("User not authenticated");
    }


    public UtenteShop saveUser(UtenteShop user) {
        return utenteShopRepository.save(user);
    }


    public List<UtenteShop> getAllUtenti() {
        return utenteShopRepository.findAll();
    }

    public UtenteShop updateUtente(Long id, UtenteShop utenteShopDetails) {
        Optional<UtenteShop> optionalUtente = utenteShopRepository.findById(id);
        if (optionalUtente.isPresent()) {
            UtenteShop utenteShop = optionalUtente.get();
            utenteShop.setFirstName(utenteShopDetails.getFirstName());
            return utenteShopRepository.save(utenteShop);
        }
        return null;
    }
    public boolean deleteUtente(String email) {
        UtenteShop optionalUtenteShop = utenteShopRepository.findByEmail(email);
        if (optionalUtenteShop != null) {
            utenteShopRepository.deleteByEmail(email);
            return true;
        }
        return false;
    }

    public UtenteShop findById(Long utenteShopId) {
        Optional<UtenteShop> utenteShop = utenteShopRepository.findById(utenteShopId);
        return utenteShop.orElse(null);
    }
}
