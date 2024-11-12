package dogshop.market.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class OrderDetailId implements Serializable {

    private Long customerOrderId;
    private Long productId;

    public OrderDetailId() {}

    public OrderDetailId(Long customerOrderId, Long productId) {
        this.customerOrderId = customerOrderId;
        this.productId = productId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderDetailId that = (OrderDetailId) o;
        return Objects.equals(customerOrderId, that.customerOrderId) &&
                Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerOrderId, productId);
    }
}