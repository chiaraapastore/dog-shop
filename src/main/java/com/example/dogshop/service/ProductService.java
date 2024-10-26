package com.example.dogshop.service;

import com.example.dogshop.entity.Category;
import com.example.dogshop.entity.Product;
import com.example.dogshop.repository.CategoryRepository;
import com.example.dogshop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;


    public Product createProduct(Product product, String categoryName) {
        Category category = categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        product.setCategory(category);
        return productRepository.save(product);
    }


    @Transactional
    public Product updateProduct(Long id, Product product, String categoryName) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        existingProduct.setProductName(product.getProductName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setAvailableQuantity(product.getAvailableQuantity());

        Category category = categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        existingProduct.setCategory(category);

        return productRepository.save(existingProduct);
    }


    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }


    public Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }


    public Page<Product> findAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findAll(pageable);
    }


    @Transactional
    public void updateAvailableQuantity(Long id, int quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getAvailableQuantity() < quantity) {
            throw new RuntimeException("Quantity not available");
        }

        product.setAvailableQuantity(product.getAvailableQuantity() - quantity);
        productRepository.save(product);
    }
}