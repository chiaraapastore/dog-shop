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

    public List<Product> getProductsInCart() {

        UtenteShop utenteShop = utenteShopRepository.findByUsername(authenticationService.getUsername());
        if (utenteShop == null) {
            throw new IllegalArgumentException("Utente non trovato");
        }

        Cart cart = cartRepository.findByUtenteShop(utenteShop)
                .orElseThrow(() -> new IllegalArgumentException("Carrello non trovato"));

        List<CartProduct> cartProducts = cartProductRepository.findByCart(cart);
        return cartProducts.stream()
                .map(CartProduct::getProduct)
                .collect(Collectors.toList());
    }



    public Cart addProductToCart(Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("La quantità deve essere maggiore di zero");
        }

        UtenteShop utenteShop = utenteShopRepository.findByUsername(authenticationService.getUsername());

        if (utenteShop == null) {
            throw new IllegalArgumentException("Utente non trovato");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Prodotto non trovato"));

        if (product.getAvailableQuantity() < quantity) {
            throw new IllegalArgumentException("Quantità non disponibile per il prodotto richiesto");
        }

        Cart cart = cartRepository.findByUtenteShop(utenteShop).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUtenteShop(utenteShop);
            return cartRepository.save(newCart);
        });

        CartProduct cartProduct = cartProductRepository.findByCartAndProduct(cart, product)
                .orElse(null);
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
        productRepository.save(product);;

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
        productRepository.save(product);

        cartProductRepository.delete(cartProduct);

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

        int currentCartQuantity = cartProduct.getQuantity(); // Quantità corrente nel carrello


        System.out.println("Quantità corrente nel carrello: " + currentCartQuantity);
        System.out.println("Nuova quantità richiesta: " + newQuantity);
        System.out.println("Disponibile nel DB prima dell'operazione: " + product.getAvailableQuantity());


        int availableQuantityIncludingCart = product.getAvailableQuantity() + currentCartQuantity;


        if (newQuantity > availableQuantityIncludingCart) {
            throw new IllegalArgumentException("Quantità richiesta non disponibile. Disponibile: " + product.getAvailableQuantity());
        }


        int quantityDifference = newQuantity - currentCartQuantity;

        product.setAvailableQuantity(product.getAvailableQuantity() - quantityDifference);


        cartProduct.setQuantity(newQuantity);

        cartProductRepository.save(cartProduct);
        productRepository.save(product);

        System.out.println("Quantità disponibile nel DB dopo l'operazione: " + product.getAvailableQuantity());
    }



}
