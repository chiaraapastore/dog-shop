package dogshop.market.service;

import dogshop.market.config.AuthenticationService;
import dogshop.market.entity.*;
import dogshop.market.repository.CartProductRepository;
import dogshop.market.repository.CartRepository;
import dogshop.market.repository.ProductRepository;
import dogshop.market.repository.UtenteShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


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
    @Autowired
    private CartProductRepository cartProductRepository;


    public List<Product> getProductsInCart() {

        UtenteShop utenteShop = utenteShopRepository.findByUsername(authenticationService.getUsername());

        Cart cart = cartRepository.findByUtenteShop(utenteShop)
                .orElseThrow(() -> new IllegalArgumentException("Carrello non trovato"));

        List<CartProduct> cartProducts = cartProductRepository.findByCart(cart);

        return cartProducts.stream()
                .map(CartProduct::getProduct)
                .collect(Collectors.toList());
    }


    public Cart addProductToCart(Long productId, int quantity) {
        UtenteShop utenteShop = utenteShopRepository.findByUsername(authenticationService.getUsername());

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        Cart cart = cartRepository.findByUtenteShop(utenteShop).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUtenteShop(utenteShop);
            return cartRepository.save(newCart);
        });

        CartProduct cartProduct = cartProductRepository.findByCartAndProduct(cart, product)
                .orElse(null);

        if (cartProduct != null) {
            cartProduct.setQuantity(cartProduct.getQuantity() + quantity);
            cartProductRepository.save(cartProduct);
        } else {
            CartProductId cartProductId = new CartProductId(cart.getId(), product.getId());
            cartProduct = new CartProduct();
            cartProduct.setId(cartProductId);
            cartProduct.setCart(cart);
            cartProduct.setProduct(product);
            cartProduct.setQuantity(quantity);
            cartProductRepository.save(cartProduct);
        }

        return cart;
    }

    public void removeProductFromCart(Long productId) {
        UtenteShop utenteShop = utenteShopRepository.findByUsername(authenticationService.getUsername());

        Cart cart = cartRepository.findByUtenteShop(utenteShop)
                .orElseThrow(() -> new IllegalArgumentException("Carrello non trovato"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Prodotto non trovato"));

        CartProduct cartProduct = cartProductRepository.findByCartAndProduct(cart, product)
                .orElseThrow(() -> new IllegalArgumentException("Prodotto non trovato nel carrello"));

        cartProductRepository.delete(cartProduct);

        List<CartProduct> remainingCartProducts = cartProductRepository.findByCart(cart);
        if (remainingCartProducts.isEmpty()) {
            cartRepository.delete(cart);
        }
    }


}