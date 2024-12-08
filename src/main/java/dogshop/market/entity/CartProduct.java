package dogshop.market.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor

public class CartProduct {

    @EmbeddedId
    private CartProductId id;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("cartId")
    private Cart cart;

    @ManyToOne
    @MapsId("productId")
    private Product product;

    private int quantity;



    public CartProductId getId() {
        return id;
    }
    public void setId(CartProductId id) {
        this.id = id;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


}
