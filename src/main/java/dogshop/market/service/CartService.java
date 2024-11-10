package dogshop.market.service;

import dogshop.market.entity.Cart;
import dogshop.market.entity.Product;
import dogshop.market.repository.CartRepository;
import dogshop.market.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private UtenteShopRepository utenteShopRepository;



    public Cart getCartByUserId(Long userId) {
        return cartRepository.findCartWithProductsByUserId(userId)
                .orElseGet(() -> createCart(userId));
    }

   public Cart getCartByUser() {
        UtenteShop currentUser = utenteShopRepository.findByUsername(authenticationService.getUsername());
        return cartRepository.findCartWithProductsByUtenteShop(currentUser);
    }


    public Cart addProductToCart(Long productId, int quantity) {
        //con questo ti recuperi l'utente corrente che ha fatto login
        UtenteShop utenteShop = utenteShopRepository.findByUsername(authenticationService.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // qui prendi il prodotto tramite l'id
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        // Qua o trovo il carrello se è già esistente o ne creo uno nuovo se è il primo
        Cart cart = cartRepository.findByUtenteShop(utenteShop).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUtenteShop(utenteShop);
            return newCart;
        });

        // qua aggiungo il prodotto al carrello
        product.setAvailableQuantity(quantity);
        cart.getProducts().add(product);

        // qua salvo il carrello
        return cartRepository.save(cart);
    }

}
