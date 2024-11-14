package dogshop.market.repository;


import dogshop.market.entity.UtenteShop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtenteShopRepository extends JpaRepository<UtenteShop, Long> {
    Optional<UtenteShop> findById(Long id);
    UtenteShop findByUsername(String username);

}
