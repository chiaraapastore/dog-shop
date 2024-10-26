package com.example.dogshop.entity;

import com.example.dogshop.repository.CategoryRepository;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor

public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String productName;
    private String description;
    private double price;
    private int availableQuantity;
    private CategoryRepository categoryRepository;

    @OneToMany(mappedBy = "productCart")
    private List<Cart> carts = new ArrayList<>();

    @OneToMany(mappedBy = "productsOrders")
    private List<Order> orders = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;  // se category è un’entità


    public void setCategory(String categoryName) {
        this.category = categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }
}