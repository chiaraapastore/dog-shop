package dogshop.market.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Entity
@Data
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Category name is required")
    private String categoryName;

    private String categoryDescription;

    private int countProduct;

    public Category() {}
    public Category(String categoryName, String categoryDescription, int countProduct) {
        this.categoryName = categoryName;
        this.categoryDescription = categoryDescription;
        this.countProduct = countProduct;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getCategoryName() {
        return categoryName;
    }
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    public String getCategoryDescription() {
        return categoryDescription;
    }
    public void setCategoryDescription(String categoryDescription) {
        this.categoryDescription = categoryDescription;
    }
    public int getCountProduct() {
        return countProduct;
    }
    public void setCountProduct(int countProduct) {
        this.countProduct = countProduct;
    }
}