package dogshop.market.controller;

import dogshop.market.entity.CustomerOrder;
import dogshop.market.entity.Product;
import dogshop.market.entity.UtenteShop;
import dogshop.market.service.OrderService;
import dogshop.market.service.ProductService;
import dogshop.market.service.UtenteShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final ProductService productService;
    private final UtenteShopService utenteShopService;

    @Autowired
    public OrderController(OrderService orderService, ProductService productService, UtenteShopService utenteShopService) {
        this.orderService = orderService;
        this.productService = productService;
        this.utenteShopService = utenteShopService;
    }

    @GetMapping("/user/{userId}")
    public List<CustomerOrder> getOrdersByUserId(@PathVariable Long userId) {
        return orderService.findOrdersByUserId(userId);
    }

    @PostMapping("/create")
    public ResponseEntity<CustomerOrder> placeOrder(
            @RequestParam Long idUtente,
            @RequestParam Long idProduct,
            @RequestParam(required = false) Double quantityProduct) {

        if (quantityProduct == null) {
            quantityProduct = 1.0;
        }

        UtenteShop utenteShop = utenteShopService.findById(idUtente);
        if (utenteShop == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        CustomerOrder order = new CustomerOrder();
        order.setUtenteShop(utenteShop);
        order.setOrderDate(LocalDate.now());
        order.setStatus("PENDING");

        double totalAmount = calcolaTotale(idProduct, quantityProduct);
        order.setTotalAmount(totalAmount);

        CustomerOrder savedOrder = orderService.save(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);
    }


    private double calcolaTotale(Long idProduct, double quantityProduct) {
        System.out.println("Looking for product with ID: " + idProduct);
        Product product = productService.findProductById(idProduct);
        if (product == null) {
            throw new IllegalArgumentException("Product not found");
        }
        return product.getPrice() * quantityProduct;
    }

}
