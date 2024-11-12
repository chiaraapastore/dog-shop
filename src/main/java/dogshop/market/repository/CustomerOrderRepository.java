package dogshop.market.repository;

import dogshop.market.entity.CustomerOrder;
import dogshop.market.entity.UtenteShop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {

    List<CustomerOrder> findByUtenteShop(UtenteShop utenteShop);

    @Query("SELECT MAX(o.id) FROM CustomerOrder o")
    Long findMaxOrderId();
}