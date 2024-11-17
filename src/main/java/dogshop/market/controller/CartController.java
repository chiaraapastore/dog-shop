package dogshop.market.controller;

import dogshop.market.entity.*;
import dogshop.market.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/cart-with-products")
    public ResponseEntity<List<Product>> getCartWithProducts() {
        List<Product> productsInCart = cartService.getProductsInCart();
        return ResponseEntity.ok(productsInCart);
    }

    @PostMapping("/addProductToCart")
    public ResponseEntity<?> addProductToCart(@RequestBody CartProductRequest request) {
        try {
            if (request.getProductId() == null || request.getQuantity() <= 0) {
                return ResponseEntity.badRequest().body("Invalid product ID or quantity.");
            }
            Cart cart = cartService.addProductToCart(request.getProductId(), request.getQuantity());
            return ResponseEntity.ok(cart);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Errore: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Errore interno: " + e.getMessage());
        }
    }


    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<String> removeProductFromCart(@PathVariable Long productId) {
        try {
            cartService.removeProductFromCart(productId);
            return ResponseEntity.ok("Prodotto rimosso dal carrello con successo.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred: " + e.getMessage());
        }
    }
}
