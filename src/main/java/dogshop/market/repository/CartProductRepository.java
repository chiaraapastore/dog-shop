package dogshop.market.repository;


import dogshop.market.entity.Cart;
import dogshop.market.entity.CartProduct;
import dogshop.market.entity.CartProductId;
import dogshop.market.entity.Product;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartProductRepository extends JpaRepository<CartProduct, CartProductId> {
    List<CartProduct> findByCart(Cart cart);

    @Query("SELECT cp FROM CartProduct cp WHERE cp.cart = :cart AND cp.product = :product")
    Optional<CartProduct> findByCartAndProduct(@Param("cart") Cart cart, @Param("product") Product product);

    Optional<CartProduct> findByCartIdAndProductId(Long cartId, Long productId);
}