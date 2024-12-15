package dogshop.market.repository;

import dogshop.market.entity.CustomerOrder;
import dogshop.market.entity.Payment;
import dogshop.market.entity.UtenteShop;
import feign.Param;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {

    List<CustomerOrder> findByUtenteShop(UtenteShop utenteShop);

    @Query("SELECT MAX(o.id) FROM CustomerOrder o")
    Long findMaxOrderId();

}