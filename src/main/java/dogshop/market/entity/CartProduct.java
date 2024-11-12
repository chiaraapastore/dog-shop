package dogshop.market.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartProduct {

    @EmbeddedId
    private CartProductId id; //chiave composta, id del carrello e id del prodotto

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("cartId")
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    private Product product;

    private int quantity;
}