package dogshop.market.controller;

import dogshop.market.entity.Cart;
import dogshop.market.entity.CartProductRequest;
import dogshop.market.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

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

    @GetMapping("/user/{userId}/with-products")
    public ResponseEntity<Cart> getCartWithProductsByUserId(@PathVariable Long userId) {
        Cart cart = cartService.getCartWithProductsByUserId(userId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/add")
    public ResponseEntity<Cart> addProductToCart(@RequestBody CartProductRequest request) {
        Cart updatedCart = cartService.addProductToCart(request.getCartId(), request.getProductId(), request.getQuantity());
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedCart);
    }
}
