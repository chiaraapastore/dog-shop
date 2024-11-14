package dogshop.market.service;

import dogshop.market.config.AuthenticationService;
import dogshop.market.entity.UtenteShop;
import dogshop.market.repository.UtenteShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UtenteShopService {

    @Autowired
    private  final AuthenticationService authenticationService;

    private final UtenteShopRepository utenteShopRepository;

    @Autowired
    public UtenteShopService(AuthenticationService authenticationService, UtenteShopRepository utenteShopRepository) {
        this.authenticationService = authenticationService;
        this.utenteShopRepository = utenteShopRepository;
    }


    public UtenteShop getUserDetails() {
        String username = authenticationService.getUsername();
        return utenteShopRepository.findByUsername(username);
    }

}