package dogshop.market.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "customer_orders")
public class CustomerOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utente_shop_id", nullable = false)
    private UtenteShop utenteShop;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @NotNull(message = "Order date is required")
    private LocalDate orderDate;

    private String status;

    private double totalAmount;

    // Dichiarazione della lista orderProducts
    @OneToMany(mappedBy = "customerOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderProduct> orderProducts = new ArrayList<>();

    // Metodo per aggiungere un OrderProduct all'ordine
    public void addOrderProduct(OrderProduct orderProduct) {
        this.orderProducts.add(orderProduct);
        orderProduct.setCustomerOrder(this); // Imposta la relazione bidirezionale
    }

    // Metodo per calcolare il totale dell'ordine
    public void calculateTotalAmount() {
        this.totalAmount = orderProducts.stream()
                .mapToDouble(op -> op.getProduct().getPrice() * op.getQuantity())
                .sum();
    }
}
