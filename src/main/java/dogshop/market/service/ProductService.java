package dogshop.market.service;
import dogshop.market.entity.Product;
import jakarta.persistence.OptimisticLockException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import dogshop.market.entity.Category;
import dogshop.market.repository.ProductRepository;
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
    public Product createProduct(Product productDetails, Long categoryId) {
        if (categoryId == null) {
            throw new IllegalArgumentException("Category ID cannot be null");
        }

        Category category = categoryService.findCategoryById(categoryId);
        productDetails.setCategory(category);

        category.setCountProduct(category.getCountProduct() + 1);
        productDetails.setSizeProduct(productDetails.getSizeProduct());
        productDetails.setProductName(productDetails.getProductName());
        productDetails.setPrice(productDetails.getPrice());
        productDetails.setAvailableQuantity(productDetails.getAvailableQuantity());

        return productRepository.save(productDetails);
    }


    @Transactional
    public Product updateProduct(Long id, Product productDetails, Long categoryId) {
        try {
            Product existingProduct = productRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            existingProduct.setProductName(productDetails.getProductName());
            existingProduct.setPrice(productDetails.getPrice());
            existingProduct.setAvailableQuantity(productDetails.getAvailableQuantity());

            Category category = categoryService.findCategoryById(categoryId);
            existingProduct.setCategory(category);

            return productRepository.save(existingProduct);
        } catch (OptimisticLockException e) {
            throw new RuntimeException("Product was modified by another transaction. Please reload and try again.");
        }
    }



    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public Product findProductById(Long id) {
        System.out.println("Looking for product with ID: " + id); // Log per debug
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
    public void updateAvailableQuantity(Long productId, int quantityChange) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        int newQuantity = product.getAvailableQuantity() - quantityChange;

        if (newQuantity < 0) {
            throw new RuntimeException("Quantity not available");
        }

        product.setAvailableQuantity(newQuantity);
        productRepository.save(product);
    }


}
