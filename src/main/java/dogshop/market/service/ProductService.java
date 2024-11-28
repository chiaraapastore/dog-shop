package dogshop.market.service;

import dogshop.market.entity.CartProduct;
import dogshop.market.entity.Product;
import dogshop.market.repository.CartProductRepository;
import dogshop.market.repository.CategoryRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import dogshop.market.entity.Category;
import dogshop.market.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final CartProductRepository cartProductRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryService categoryService, CartProductRepository cartProductRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
        this.cartProductRepository = cartProductRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public Product createProduct(Product productDetails, Long categoryId) {
        System.out.println("Looking for category with ID: " + categoryId);

        Category category = categoryService.findCategoryById(categoryId);
        if (category == null) {
            System.out.println("Category not found with ID: " + categoryId);
            throw new RuntimeException("Category not found with ID: " + categoryId);
        }
        System.out.println("Found category: " + category.getCategoryName());

        productDetails.setCategory(category);
        productDetails.setCategoryName(category.getCategoryName());


        Optional<Product> existingProductOpt = productRepository.findByProductNameAndCategory(productDetails.getProductName(), category);
        if (existingProductOpt.isPresent()) {
            Product existingProduct = existingProductOpt.get();
            existingProduct.setAvailableQuantity(existingProduct.getAvailableQuantity() + productDetails.getAvailableQuantity());
            return productRepository.save(existingProduct);
        } else {
            category.setCountProduct(category.getCountProduct() + 1);

            if (productDetails.getVersion() == null) {
                productDetails.setVersion(0);
            }

            return productRepository.save(productDetails);
        }
    }



    @Transactional
    public Product updateProduct(Long id, Product productDetails, Long categoryId) {

        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));


        Category category = categoryService.findCategoryById(categoryId);
        if (category == null) {
            throw new RuntimeException("Category not found with ID: " + categoryId);
        }


        existingProduct.setProductName(productDetails.getProductName());
        existingProduct.setPrice(productDetails.getPrice());
        existingProduct.setAvailableQuantity(productDetails.getAvailableQuantity());
        existingProduct.setCategory(category);


        return productRepository.save(existingProduct);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }


    public Product findProductById(Long id) {
        System.out.println("Looking for product with ID: " + id);
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public Page<Product> findAllProducts(int page, int size, String sortBy, String sortDir, String category, String sizeProduct) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDir), sortBy));

        if (category != null && !category.isEmpty() && sizeProduct != null && !sizeProduct.isEmpty()) {
            return productRepository.findBySizeProduct(sizeProduct, pageable, category);
        } else if (category != null && !category.isEmpty()) {
            return productRepository.findByCategory_CategoryName(category, pageable);
        } else if (sizeProduct != null && !sizeProduct.isEmpty()) {
            return productRepository.findBySizeProduct(sizeProduct, pageable, category);
        } else {
            return productRepository.findAll(pageable);
        }
    }

    @Transactional
    public void updateProductQuantityInCart(Long productId, Long cartId, int quantityChange) {
        if (quantityChange == 0) {
            throw new IllegalArgumentException("La modifica della quantità deve essere diversa da zero");
        }

        CartProduct cartProduct = cartProductRepository.findByCartIdAndProductId(cartId, productId)
                .orElseThrow(() -> new RuntimeException("Prodotto non trovato nel carrello"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Prodotto non trovato"));

        int newAvailableQuantity = product.getAvailableQuantity() - quantityChange;
        if (newAvailableQuantity < 0) {
            throw new RuntimeException("Quantità non disponibile in stock");
        }

        product.setAvailableQuantity(newAvailableQuantity);
        productRepository.save(product);

        int newCartQuantity = cartProduct.getQuantity() + quantityChange;
        if (newCartQuantity <= 0) {
            cartProductRepository.delete(cartProduct);
        } else {
            cartProduct.setQuantity(newCartQuantity);
            cartProductRepository.save(cartProduct);
        }
    }

    public List<Product> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return productRepository.findAll();
        } else {
            return productRepository.findByProductNameContainingIgnoreCase(keyword);
        }
    }
}
