package dogshop.market.controller;

import dogshop.market.entity.*;
import dogshop.market.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/cart-with-products")
    public ResponseEntity<List<CartProduct>> getCartWithProducts() {
        List<CartProduct> productsInCart = cartService.getProductsInCart();
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
    public ResponseEntity<Void> removeProductFromCart(@PathVariable Long productId) {
        cartService.removeProductFromCart(productId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update-quantity/{productId}")
    public ResponseEntity<Void> updateProductQuantity(
            @PathVariable Long productId,
            @RequestParam int quantity) {
        cartService.updateProductQuantityInCart(productId, quantity);
        return ResponseEntity.ok().build();
    }


}
