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
    private final OrderDetailRepository orderDetailRepository;
    private final PaymentRepository paymentRepository;
    private final OrderProductRepository orderProductRepository;


    public CustomerOrderService(CustomerOrderRepository customerOrderRepository,AuthenticationService authenticationService, UtenteShopRepository utenteShopRepository, OrderDetailRepository  orderDetailRepository, PaymentRepository paymentRepository, OrderProductRepository orderProductRepository) {
        this.customerOrderRepository = customerOrderRepository;
        this.authenticationService = authenticationService;
        this.utenteShopRepository = utenteShopRepository;
        this.orderDetailRepository = orderDetailRepository;
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

        // Rimuovi i dettagli dell'ordine
        List<OrderDetail> orderDetails = orderDetailRepository.findByCustomerOrderId(orderId);
        if (orderDetails != null) {
            for (OrderDetail detail : orderDetails) {
                orderDetailRepository.delete(detail);
            }
        }
        // Rimuovi i prodotti associati all'ordine
        if (order.getOrderProducts() != null) {
            for (OrderProduct product : order.getOrderProducts()) {
                orderProductRepository.delete(product);
            }
        }

        // Rimuovi il pagamento associato
        if (order.getPayment() != null) {
            paymentRepository.delete(order.getPayment());
        }

        // Elimina l'ordine
        customerOrderRepository.delete(order);
    }
}
