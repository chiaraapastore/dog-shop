package com.example.dogshop.controller;

import com.example.dogshop.entity.Product;
import com.example.dogshop.service.ProductService;
import com.example.dogshop.service.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final UtenteService userService;

    @Autowired
    public ProductController(ProductService productService, UtenteService userService) {
        this.productService = productService;
        this.userService = userService;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        String username = userService.getAuthenticatedUsername();
        System.out.println("Updating product for user: " + username);

        Product updatedProduct = productService.updateProduct(id, productDetails);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> findProductById(@PathVariable Long id) {
        Product product = productService.findProductById(id);
        return ResponseEntity.ok(product);
    }


    @GetMapping
    public ResponseEntity<Page<Product>> findAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Product> products = productService.findAllProducts(page, size);
        return ResponseEntity.ok(products);
    }

    @PutMapping("/{id}/update-quantity")
    public ResponseEntity<Void> updateAvailableQuantity(
            @PathVariable Long id,
            @RequestParam int quantity) {
        productService.updateAvailableQuantity(id, quantity);
        return ResponseEntity.ok().build();
    }
}
