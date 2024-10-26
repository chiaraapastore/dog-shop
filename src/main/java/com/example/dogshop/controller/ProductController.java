package com.example.dogshop.controller;

import com.example.dogshop.entity.Product ;
import com.example.dogshop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    public ResponseEntity<Product > createProduct(@RequestBody Product  product) {
        Product  newProduct = productService.createProduct(product);
        return new ResponseEntity<>(newProduct, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product > updateProduct(@PathVariable Long id, @RequestBody Product  product) {
        Product  updatedProduct = productService.updateProduct(id, product);
        return ResponseEntity.ok(updatedProduct);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product > findProductById(@PathVariable Long id) {
        Product  product = productService.findProductById(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping
    public ResponseEntity<Page<Product >> findAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Product > products = productService.findAllProducts(page, size);
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
