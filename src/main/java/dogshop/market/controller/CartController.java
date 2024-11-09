package dogshop.market.controller;

import dogshop.market.entity.Cart;
import dogshop.market.entity.CartProductRequest;
import dogshop.market.entity.CustomerOrder;
import dogshop.market.service.CartService;
import dogshop.market.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<Cart> createCart(@RequestParam Long userId) {
        Cart newCart = cartService.createCart(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCart);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Cart> getCartByUserId(@PathVariable Long userId) {
        Cart cart = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/add")
    public ResponseEntity<Cart> addProductToCart(@RequestBody CartProductRequest request) {
        System.out.println("Ricevuto richiesta: cartId=" + request.getCartId() + ", productId=" + request.getProductId() + ", quantity=" + request.getQuantity());
        Cart updatedCart = cartService.addProductToCart(request.getCartId(), request.getProductId(), request.getQuantity());
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedCart);
    }


    @PostMapping("/checkout/{cartId}")
    public ResponseEntity<CustomerOrder> checkout(@PathVariable Long cartId) {
        CustomerOrder newOrder = orderService.createOrderFromCart(cartId);
        return ResponseEntity.status(HttpStatus.CREATED).body(newOrder);
    }
}
