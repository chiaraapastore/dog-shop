package com.example.dogshop.service;
import org.springframework.stereotype.Service;
import com.example.dogshop.entity.Category;
import com.example.dogshop.entity.Product;
import com.example.dogshop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import jakarta.transaction.Transactional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    @Autowired
    public ProductService(ProductRepository productRepository, CategoryService categoryService) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
    }

    @Transactional
    public Product updateProduct(Long id, Product productDetails) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        existingProduct.setProductName(productDetails.getProductName());
        existingProduct.setPrice(productDetails.getPrice());
        existingProduct.setAvailableQuantity(productDetails.getAvailableQuantity());

        Category category = categoryService.findCategoryById(productDetails.getCategory().getId());
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
