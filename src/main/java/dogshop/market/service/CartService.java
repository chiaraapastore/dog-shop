package dogshop.market.service;

import dogshop.market.config.AuthenticationService;
import dogshop.market.entity.Cart;
import dogshop.market.entity.Category;
import dogshop.market.entity.Product;
import dogshop.market.entity.UtenteShop;
import dogshop.market.repository.CartRepository;
import dogshop.market.repository.ProductRepository;
import dogshop.market.repository.UtenteShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private UtenteShopRepository utenteShopRepository;
    @Autowired
    private ProductRepository productRepository;

    public Cart getCartByUserId(Long userId) {
        UtenteShop currentUser = utenteShopRepository.findByUsername(authenticationService.getUsername());
        return cartRepository.findCartWithProductsByUserId(userId);
    }

    public Cart createCart(Long userId) {
        UtenteShop utenteShop = utenteShopRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = new Cart();
        cart.setUtenteShop(utenteShop);
        return cartRepository.save(cart);
    }

    public Cart getCartWithProductsByUserId(Long userId) {
        return cartRepository.findCartWithProductsByUserId(userId);
    }

    public Cart addProductToCart(Long cartId, Long productId, int quantity) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Crea un nuovo oggetto Product per rappresentare l'aggiunta nel carrello
        cart.getCartProducts().add(product);

        // Salva il carrello aggiornato
        return cartRepository.save(cart);
    }


}
