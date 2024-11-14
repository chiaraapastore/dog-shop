package dogshop.market.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
@Getter
@Setter
@Embeddable
public class CartProductId implements Serializable {

    private Long cartId;
    private Long productId;

    public CartProductId() {}

    public CartProductId(Long cartId, Long productId) {
        this.cartId = cartId;
        this.productId = productId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartProductId that = (CartProductId) o;
        return cartId.equals(that.cartId) && productId.equals(that.productId);
    }

    @Override
    public int hashCode() {
        return 31 * cartId.hashCode() + productId.hashCode();
    }
}