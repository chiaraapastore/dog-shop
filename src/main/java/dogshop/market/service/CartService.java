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

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final AuthenticationService authenticationService;
    private final UtenteShopRepository utenteShopRepository;
    private final ProductRepository productRepository;
    private final CartProductRepository cartProductRepository;
    private final ProductService productService;

    public CartService(CartRepository cartRepository, AuthenticationService authenticationService,
                       UtenteShopRepository utenteShopRepository, ProductRepository productRepository,
                       CartProductRepository cartProductRepository,ProductService productService) {
        this.cartRepository = cartRepository;
        this.authenticationService = authenticationService;
        this.utenteShopRepository = utenteShopRepository;
        this.productRepository = productRepository;
        this.cartProductRepository = cartProductRepository;
        this.productService = productService;
    }

    public List<CartProduct> getProductsInCart() {
        UtenteShop utenteShop = utenteShopRepository.findByUsername(authenticationService.getUsername());
        if (utenteShop == null) {
            throw new IllegalArgumentException("Utente non trovato");
        }

        Cart cart = cartRepository.findByUtenteShop(utenteShop)
                .orElseThrow(() -> new IllegalArgumentException("Carrello non trovato"));
        List<CartProduct> cartProducts = cartProductRepository.findByCart(cart);
        System.out.println("Prodotti carrello"+cartProducts);
        return cartProductRepository.findByCart(cart);
    }

    @Transactional
    public Cart addProductToCart(Long productId, int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("La quantità deve essere almeno 1");
        }


        UtenteShop utenteShop = utenteShopRepository.findByUsername(authenticationService.getUsername());
        if (utenteShop == null) {
            throw new IllegalArgumentException("Utente non trovato");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Prodotto non trovato"));

        if (product.getAvailableQuantity() <= 0) {
            productRepository.delete(product);
            productRepository.flush(); // Forza l'eliminazione immediata
            throw new IllegalArgumentException("Il prodotto non è disponibile ed è stato eliminato.");
        }

        if (product.getAvailableQuantity() < quantity) {
            throw new IllegalArgumentException("Quantità non disponibile per il prodotto richiesto");
        }

        Cart cart = cartRepository.findByUtenteShop(utenteShop).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUtenteShop(utenteShop);
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
            cartProductRepository.save(cartProduct);
        }

        product.setAvailableQuantity(product.getAvailableQuantity() - quantity);

        productRepository.save(product);

        return cart;
    }


    @Transactional
    public void removeProductFromCart(Long productId) {
        UtenteShop utenteShop = utenteShopRepository.findByUsername(authenticationService.getUsername());
        if (utenteShop == null) {
            throw new IllegalArgumentException("Utente non trovato");
        }

        Cart cart = cartRepository.findByUtenteShop(utenteShop)
                .orElseThrow(() -> new IllegalArgumentException("Carrello non trovato"));

        CartProduct cartProduct = cartProductRepository.findByCartAndProduct(cart, productRepository.findById(productId)
                        .orElseThrow(() -> new IllegalArgumentException("Prodotto non trovato")))
                .orElseThrow(() -> new IllegalArgumentException("Prodotto non trovato nel carrello"));

        Product product = cartProduct.getProduct();
        product.setAvailableQuantity(product.getAvailableQuantity() + cartProduct.getQuantity());

        cartProductRepository.delete(cartProduct);

        if (cartProductRepository.findByCart(cart).isEmpty()) {
            cartRepository.delete(cart);
        }


        if (product.getAvailableQuantity() <= 0) {
            productRepository.delete(product);
        } else {
            productRepository.save(product);
        }

        if (cartProductRepository.findByCart(cart).isEmpty()) {
            cartRepository.delete(cart);
        }
    }

    @Transactional
    public void updateProductQuantityInCart(Long productId, int newQuantity) {
        UtenteShop utenteShop = utenteShopRepository.findByUsername(authenticationService.getUsername());
        if (utenteShop == null) {
            throw new IllegalArgumentException("Utente non trovato");
        }

        Cart cart = cartRepository.findByUtenteShop(utenteShop)
                .orElseThrow(() -> new IllegalArgumentException("Carrello non trovato"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Prodotto non trovato"));

        CartProduct cartProduct = cartProductRepository.findByCartAndProduct(cart, product)
                .orElseThrow(() -> new IllegalArgumentException("Prodotto non trovato nel carrello"));

        int currentCartQuantity = cartProduct.getQuantity();
        int availableQuantityIncludingCart = product.getAvailableQuantity() + currentCartQuantity;

        if (newQuantity > availableQuantityIncludingCart) {
            throw new IllegalArgumentException("Quantità richiesta non disponibile");
        }

        int quantityDifference = newQuantity - currentCartQuantity;
        product.setAvailableQuantity(product.getAvailableQuantity() - quantityDifference);

        cartProduct.setQuantity(newQuantity);

        cartProductRepository.save(cartProduct);

        if (product.getAvailableQuantity() <= 0) {
            System.out.println("Prodotto eliminato dal database poiché esaurito: " + product.getId());
        } else {
            productRepository.save(product);
        }
    }
}