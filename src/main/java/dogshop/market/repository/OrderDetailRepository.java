package dogshop.market.repository;

import dogshop.market.entity.OrderDetail;
import dogshop.market.entity.OrderDetailId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, OrderDetailId> {
    List<OrderDetail> findByCustomerOrderId(Long customerOrderId);
}

