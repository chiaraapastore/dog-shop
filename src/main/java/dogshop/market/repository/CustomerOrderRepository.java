package dogshop.market.repository;

import dogshop.market.entity.CustomerOrder;
import dogshop.market.entity.UtenteShop;
import feign.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {

    List<CustomerOrder> findByUtenteShop(UtenteShop utenteShop);

    @Query("SELECT MAX(o.id) FROM CustomerOrder o")
    Long findMaxOrderId();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM CustomerOrder o WHERE o.id = :id")
    Optional<CustomerOrder> findByIdWithLock(@Param("id") Long id);


}