package dogshop.market.service;
import dogshop.market.entity.CartProduct;
import dogshop.market.entity.Product;
import dogshop.market.repository.CartProductRepository;
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

    public ProductService(ProductRepository productRepository, CategoryService categoryService, CartProductRepository cartProductRepository) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
        this.cartProductRepository = cartProductRepository;
    }


    @Transactional
    public Product createProduct(Product productDetails, Long categoryId) {
        if (categoryId == null) {
            throw new IllegalArgumentException("Category ID cannot be null");
        }

        Category category = categoryService.findCategoryById(categoryId);

        Optional<Product> existingProductOpt = productRepository.findByProductNameAndCategory(productDetails.getProductName(), category);
        if (existingProductOpt.isPresent()) {
            Product existingProduct = existingProductOpt.get();
            existingProduct.setAvailableQuantity(existingProduct.getAvailableQuantity() + productDetails.getAvailableQuantity());
            return productRepository.save(existingProduct);
        } else {
            productDetails.setCategory(category);
            category.setCountProduct(category.getCountProduct() + 1);

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
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();

            if (product.getAvailableQuantity() > 1) {
                product.setAvailableQuantity(product.getAvailableQuantity() - 1);
                productRepository.save(product);
            } else {

                product.setAvailableQuantity(0);
                productRepository.save(product);

                Category category = product.getCategory();
                long productsCount = productRepository.countByCategory(category);
                if (productsCount == 0) {
                    categoryService.deleteCategory(category.getId());
                }
            }
        } else {
            throw new IllegalArgumentException("Product not found with id: " + id);
        }
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
