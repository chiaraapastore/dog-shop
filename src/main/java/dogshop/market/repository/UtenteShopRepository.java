package dogshop.market.repository;


import dogshop.market.entity.UtenteShop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtenteShopRepository extends JpaRepository<UtenteShop, Long> {
    UtenteShop findByEmail(String username);
    void deleteByEmail(String email);
    Optional<UtenteShop> findById(Long id);
    UtenteShop findByUsername(String username);

}
