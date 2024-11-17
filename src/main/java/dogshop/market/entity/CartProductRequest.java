package dogshop.market.entity;

import lombok.*;


@NoArgsConstructor
public class CartProductRequest {
    private Long productId;
    private int quantity;

    public CartProductRequest(Long productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public CartProductRequest(CartProduct cartProduct) {
        this.productId = cartProduct.getProduct().getId();
        this.quantity = cartProduct.getQuantity();
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}


