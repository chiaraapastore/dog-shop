package dogshop.market.repository;

import dogshop.market.entity.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
    void delete(OrderProduct orderProduct);
}
