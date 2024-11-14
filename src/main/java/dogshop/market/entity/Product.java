package dogshop.market.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Product name is required")
    private String productName;

    private String description;

    @Min(value = 0, message = "Price cannot be negative")
    private double price;

    @Min(value = 0, message = "Quantity cannot be negative")
    private int availableQuantity;

    @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    private String categoryName;

    private String sizeProduct;

}
