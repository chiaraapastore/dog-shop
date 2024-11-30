package dogshop.market.repository;

import dogshop.market.entity.Category;
import dogshop.market.entity.Product;
import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByCategory_CategoryName(String categoryName, Pageable pageable);
    @Query("SELECT p FROM Product p WHERE (:category IS NULL OR p.category.categoryName = :category) AND (:sizeProduct IS NULL OR p.sizeProduct = :sizeProduct)")
    Page<Product> findBySizeProduct(@Param("sizeProduct") String sizeProduct, Pageable pageable, @Param("category") String category);
    List<Product> findByProductNameContainingIgnoreCase(String keyword);
    Optional<Product> findByProductNameAndCategory(String productName, Category category);
    @Query("SELECT COUNT(p) FROM Product p WHERE p.category = :category")
    int countByCategory(Category category);
}
