package dogshop.market.repository;


import dogshop.market.entity.UtenteShop;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface UtenteShopRepository extends JpaRepository<UtenteShop, Long> {
    UtenteShop findByUsername(@Param("username")String username);

}
