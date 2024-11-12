package dogshop.market.service;
import dogshop.market.config.AuthenticationService;
import dogshop.market.entity.*;
import dogshop.market.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class CustomerOrderService {

    @Autowired
    private CustomerOrderRepository customerOrderRepository;
    @Autowired
    private final AuthenticationService authenticationService;
    @Autowired
    private UtenteShopRepository utenteShopRepository;


    public CustomerOrderService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Transactional
    public List<CustomerOrder> findOrdersByAuthenticatedUser() {
        UtenteShop utenteShop = utenteShopRepository.findByUsername(authenticationService.getUsername());
        return customerOrderRepository.findByUtenteShop(utenteShop);
    }


}