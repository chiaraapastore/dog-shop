package com.example.dogshop.service;

import com.example.dogshop.entity.Product;
import com.example.dogshop.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;


    public Product createProduct(Product  product) {
        return productRepository.save(product);
    }


    @Transactional
    public Product updateProduct(Long id, Product  product) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        existingProduct.setProductName(product.getProductName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setAvailableQuantity(product.getAvailableQuantity());
        existingProduct.setCategory(String.valueOf(product.getCategory()));

        return productRepository.save(existingProduct);
    }


    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public Product  findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }


    public Page<Product > findAllProducts(int page, int size) {
        return productRepository.findAll(PageRequest.of(page, size));
    }


    @Transactional
    public void updateAvailableQuantity(Long id, int quantity) {
        Product  product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getAvailableQuantity() < quantity) {
            throw new RuntimeException("Quantity not available");
        }

        product.setAvailableQuantity(product.getAvailableQuantity() - quantity);
        productRepository.save(product);
    }
}
