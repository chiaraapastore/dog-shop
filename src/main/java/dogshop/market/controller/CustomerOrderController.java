package dogshop.market.controller;

import dogshop.market.entity.CustomerOrder;
import dogshop.market.service.CustomerOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class CustomerOrderController {
    private final CustomerOrderService customerOrderService;

    @Autowired
    public CustomerOrderController(CustomerOrderService customerOrderService) {
        this.customerOrderService = customerOrderService;

    }

    @GetMapping("/myOrders")
    public ResponseEntity<List<CustomerOrder>> getMyOrders() {
        List<CustomerOrder> orders = customerOrderService.findOrdersByAuthenticatedUser();
        return ResponseEntity.ok(orders);
    }

}