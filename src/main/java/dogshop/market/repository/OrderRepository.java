package dogshop.market.repository;

import dogshop.market.entity.CustomerOrder;
import dogshop.market.entity.OrderProduct;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<CustomerOrder, Long> {
    List<CustomerOrder> findByUtenteShopId(Long userId);
    @Query("SELECT o FROM CustomerOrder o WHERE o.status = :status")
    List<CustomerOrder> findByStatus(@Param("status") String status);
}


