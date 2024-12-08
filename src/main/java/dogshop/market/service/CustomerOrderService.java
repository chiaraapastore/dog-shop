package dogshop.market.service;
import dogshop.market.config.AuthenticationService;
import dogshop.market.entity.*;
import dogshop.market.repository.*;
import jakarta.persistence.criteria.Order;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class CustomerOrderService {

    private final CustomerOrderRepository customerOrderRepository;
    private final AuthenticationService authenticationService;
    private final UtenteShopRepository utenteShopRepository;
    private final PaymentRepository paymentRepository;
    private final OrderProductRepository orderProductRepository;


    public CustomerOrderService(CustomerOrderRepository customerOrderRepository,AuthenticationService authenticationService, UtenteShopRepository utenteShopRepository, PaymentRepository paymentRepository, OrderProductRepository orderProductRepository) {
        this.customerOrderRepository = customerOrderRepository;
        this.authenticationService = authenticationService;
        this.utenteShopRepository = utenteShopRepository;
        this.paymentRepository = paymentRepository;
        this.orderProductRepository = orderProductRepository;
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



        if (order.getOrderProducts() != null) {
            for (OrderProduct product : order.getOrderProducts()) {
                orderProductRepository.delete(product);
            }
        }


        if (order.getPayment() != null) {
            paymentRepository.delete(order.getPayment());
        }


        customerOrderRepository.delete(order);
    }



}
