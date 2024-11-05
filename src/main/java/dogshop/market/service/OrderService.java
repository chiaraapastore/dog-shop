package dogshop.market.service;
import dogshop.market.entity.CustomerOrder;
import dogshop.market.repository.OrderRepository;
import dogshop.market.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    private ProductRepository productRepository;
    private OrderService orderService;

    public List<CustomerOrder> findOrdersByUserId(Long userId) {
        return orderRepository.findByUtenteShopId(userId);
    }

    public CustomerOrder saveOrder(CustomerOrder order) {
        return orderRepository.save(order);
    }



}

