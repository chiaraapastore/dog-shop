package dogshop.market.repository;

import dogshop.market.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    @Query("SELECT c FROM Cart c JOIN FETCH c.cartItems ci JOIN FETCH ci.product WHERE c.utenteShop.id = :userId")
    Optional<Cart> findCartWithProductsByUserId(Long userId);
    @Query("SELECT c FROM Cart c WHERE c.utenteShop.id = :userId")
    Optional<Cart> findByUserId(Long userId);
}
