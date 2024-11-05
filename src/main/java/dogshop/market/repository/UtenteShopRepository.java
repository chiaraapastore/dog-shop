package dogshop.market.repository;


import dogshop.market.entity.UtenteShop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UtenteShopRepository extends JpaRepository<UtenteShop, Long> {
    UtenteShop findByUsername(String username);
    UtenteShop findByEmail(String username);
    void deleteByEmail(String email);
}
