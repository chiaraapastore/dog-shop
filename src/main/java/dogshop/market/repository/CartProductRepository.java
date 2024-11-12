package dogshop.market.repository;


import dogshop.market.entity.Cart;
import dogshop.market.entity.CartProduct;
import dogshop.market.entity.CartProductId;
import dogshop.market.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartProductRepository extends JpaRepository<CartProduct, CartProductId> {
    List<CartProduct> findByCart(Cart cart);

    Optional<CartProduct> findByCartAndProduct(Cart cart, Product product);

}