package dogshop.market.service;

import dogshop.market.config.AuthenticationService;
import dogshop.market.entity.UtenteShop;
import dogshop.market.repository.UtenteShopRepository;
import org.springframework.stereotype.Service;

@Service
public class UtenteShopService {

    private  final AuthenticationService authenticationService;
    private final UtenteShopRepository utenteShopRepository;

    public UtenteShopService(AuthenticationService authenticationService, UtenteShopRepository utenteShopRepository) {
        this.authenticationService = authenticationService;
        this.utenteShopRepository = utenteShopRepository;
    }


    public UtenteShop getUserDetails() {
        String username = authenticationService.getUsername();
        return utenteShopRepository.findByUsername(username);
    }

}