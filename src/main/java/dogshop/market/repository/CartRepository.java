package dogshop.market.repository;


import dogshop.market.entity.Cart;
import dogshop.market.entity.UtenteShop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findCartWithProductsByUtenteShop(UtenteShop utenteShop);
    Optional<Cart> findByUtenteShop(UtenteShop utenteShop);

}