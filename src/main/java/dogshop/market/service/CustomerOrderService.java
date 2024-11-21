package dogshop.market.service;
import dogshop.market.config.AuthenticationService;
import dogshop.market.entity.*;
import dogshop.market.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class CustomerOrderService {

    private final CustomerOrderRepository customerOrderRepository;
    private final AuthenticationService authenticationService;
    private final UtenteShopRepository utenteShopRepository;


    public CustomerOrderService(CustomerOrderRepository customerOrderRepository,AuthenticationService authenticationService, UtenteShopRepository utenteShopRepository) {
        this.customerOrderRepository = customerOrderRepository;
        this.authenticationService = authenticationService;
        this.utenteShopRepository = utenteShopRepository;
    }

    @Transactional
    public List<CustomerOrder> findOrdersByAuthenticatedUser() {
        UtenteShop utenteShop = utenteShopRepository.findByUsername(authenticationService.getUsername());
        return customerOrderRepository.findByUtenteShop(utenteShop);
    }

    @Transactional
public void cancelOrder(Long orderId) {
    CustomerOrder order = customerOrderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));

    // elimino dalla tabella orderProduct
    List<OrderProduct> orderProducts = order.getOrderProducts(); 
    if (orderProducts != null) {
        for (OrderProduct orderProduct : orderProducts) {
            orderProductRepository.delete(orderProduct); 
        }
    }

    // elimino il pagamento associato all'ordine
    if (order.getPayment() != null) {
        paymentRepository.delete(order.getPayment()); 
    }

    // ed elimino l'ordine con il dettaglio dalla 
    orderDetailRepository.deleteByCustomerOrderId(orderId); 
    // dopo aver dissociato/eliminato gli ordini dalle altre tabelle elimino l'ordine che voglio cancellare
    customerOrderRepository.delete(order);
}




}
