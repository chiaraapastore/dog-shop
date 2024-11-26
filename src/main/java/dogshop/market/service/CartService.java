package dogshop.market.service;

import dogshop.market.config.AuthenticationService;
import dogshop.market.entity.*;
import dogshop.market.repository.CartProductRepository;
import dogshop.market.repository.CartRepository;
import dogshop.market.repository.ProductRepository;
import dogshop.market.repository.UtenteShopRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final AuthenticationService authenticationService;
    private final UtenteShopRepository utenteShopRepository;
    private final ProductRepository productRepository;
    private final CartProductRepository cartProductRepository;

    public CartService(CartRepository cartRepository, AuthenticationService authenticationService, UtenteShopRepository utenteShopRepository, ProductRepository productRepository, CartProductRepository cartProductRepository) {
        this.cartRepository = cartRepository;
        this.authenticationService = authenticationService;
        this.utenteShopRepository = utenteShopRepository;
        this.productRepository = productRepository;
        this.cartProductRepository = cartProductRepository;
    }

    private UtenteShop getAuthenticatedUser() {
        String username = authenticationService.getUsername();
        return Optional.ofNullable(utenteShopRepository.findByUsername(username))
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));
    }

    private Cart getCartForUser(UtenteShop user) {
        return cartRepository.findByUtenteShop(user)
                .orElseThrow(() -> new IllegalArgumentException("Carrello non trovato"));
    }

    private Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Prodotto non trovato"));
    }

    public List<CartProduct> getProductsInCart() {
        UtenteShop user = getAuthenticatedUser();
        Cart cart = getCartForUser(user);

        return cartProductRepository.findByCart(cart);
    }


    public Cart addProductToCart(Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("La quantità deve essere maggiore di zero");
        }

        UtenteShop user = getAuthenticatedUser();
        Product product = getProductById(productId);

        if (product.getAvailableQuantity() < quantity) {
            throw new IllegalArgumentException("Quantità non disponibile per il prodotto richiesto");
        }

        Cart cart = cartRepository.findByUtenteShop(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUtenteShop(user);
            return cartRepository.save(newCart);
        });

        CartProduct cartProduct = cartProductRepository.findByCartAndProduct(cart, product).orElse(null);

        if (cartProduct != null) {
            int newQuantity = cartProduct.getQuantity() + quantity;
            if (newQuantity > product.getAvailableQuantity()) {
                throw new IllegalArgumentException("Quantità totale non disponibile per questo prodotto");
            }
            cartProduct.setQuantity(newQuantity);
        } else {
            cartProduct = new CartProduct(new CartProductId(cart.getId(), product.getId()), cart, product, quantity);
        }

        cartProductRepository.save(cartProduct);

        product.setAvailableQuantity(product.getAvailableQuantity() - quantity);
        productRepository.save(product);

        return cart;
    }

    @Transactional
    public void removeProductFromCart(Long productId) {
        UtenteShop user = getAuthenticatedUser();
        Cart cart = getCartForUser(user);
        Product product = getProductById(productId);

        CartProduct cartProduct = cartProductRepository.findByCartAndProduct(cart, product)
                .orElseThrow(() -> new IllegalArgumentException("Prodotto non trovato nel carrello"));

        product.setAvailableQuantity(product.getAvailableQuantity() + cartProduct.getQuantity());
        productRepository.save(product);

        cartProductRepository.delete(cartProduct);

        if (cartProductRepository.findByCart(cart).isEmpty()) {
            cartRepository.delete(cart);
        }
    }

    @Transactional
    public void updateProductQuantityInCart(Long productId, int newQuantity) {
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("La quantità deve essere maggiore di zero");
        }

        UtenteShop user = getAuthenticatedUser();
        Cart cart = getCartForUser(user);
        Product product = getProductById(productId);

        CartProduct cartProduct = cartProductRepository.findByCartAndProduct(cart, product)
                .orElseThrow(() -> new IllegalArgumentException("Prodotto non trovato nel carrello"));

        if (newQuantity > product.getAvailableQuantity() + cartProduct.getQuantity()) {
            throw new IllegalArgumentException("Quantità richiesta non disponibile");
        }

        int quantityDifference = newQuantity - cartProduct.getQuantity();
        product.setAvailableQuantity(product.getAvailableQuantity() - quantityDifference);
        productRepository.save(product);

        cartProduct.setQuantity(newQuantity);
        cartProductRepository.save(cartProduct);
    }
}
