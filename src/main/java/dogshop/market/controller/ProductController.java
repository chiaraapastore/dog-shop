package dogshop.market.controller;

import dogshop.market.entity.Product;
import dogshop.market.service.ProductService;
import dogshop.market.service.UtenteShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final UtenteShopService userService;

    @Autowired
    public ProductController(ProductService productService, UtenteShopService userService) {
        this.productService = productService;
        this.userService = userService;
    }

    @PutMapping("/{id}/{categoryId}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product productDetails, @PathVariable Long categoryId) {
        Product updatedProduct = productService.updateProduct(id, productDetails, categoryId);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/create/{categoryId}")
    public ResponseEntity<Product> createProduct(@RequestBody Product productDetails, @PathVariable Long categoryId) {
        Product createdProduct = productService.createProduct(productDetails,categoryId);
        return ResponseEntity.ok(createdProduct);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Product> findProductById(@PathVariable Long id) {
        Product product = productService.findProductById(id);
        return ResponseEntity.ok(product);
    }


    @GetMapping("/list")
    public ResponseEntity<Page<Product>> findAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "productName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false)String category,
            @RequestParam(value = "sizeProduct", required = false) String sizeProduct) {
        System.out.println("Category: " + category);
        System.out.println("Size Product: " + sizeProduct);

        Page<Product> products = productService.findAllProducts(page, size, sortBy, sortDir, category, sizeProduct);
        return ResponseEntity.ok(products);
    }


    @PutMapping("/update-quantity/{id}")
    public ResponseEntity<Void> updateAvailableQuantity(
            @PathVariable Long id,
            @RequestParam int quantity) {
        productService.updateAvailableQuantity(id, quantity);
        return ResponseEntity.ok().build();
    }


}
