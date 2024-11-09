package dogshop.market.service;

import dogshop.market.entity.Cart;
import dogshop.market.entity.CustomerOrder;
import dogshop.market.entity.OrderProduct;
import dogshop.market.repository.CartRepository;
import dogshop.market.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;

    public CustomerOrder createOrderFromCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Carrello non trovato"));

        CustomerOrder order = new CustomerOrder();
        order.setUtenteShop(cart.getUtenteShop());
        order.setOrderDate(java.time.LocalDate.now());
        order.setStatus("PENDING");

        // Aggiungi i prodotti del carrello all'ordine e imposta la relazione
        for (var cartItem : cart.getCartItems()) {
            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setProduct(cartItem.getProduct());
            orderProduct.setQuantity(cartItem.getQuantity());
            order.addOrderProduct(orderProduct); // Usa il metodo di aggiunta in CustomerOrder
        }

        // Calcola il totale dell'ordine
        order.calculateTotalAmount();
        order = orderRepository.save(order);

        // Svuota il carrello
        cart.clear();
        cartRepository.save(cart);

        return order;
    }

    public CustomerOrder save(CustomerOrder order) {
        return orderRepository.save(order);
    }

    public List<CustomerOrder> findOrdersByUserId(Long userId) {
        return orderRepository.findByUtenteShopId(userId);
    }
}
