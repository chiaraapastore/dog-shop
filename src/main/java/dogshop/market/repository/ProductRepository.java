package dogshop.market.repository;
import dogshop.market.entity.Product;
import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByCategory_CategoryName(String categoryName, Pageable pageable);
    @Query("SELECT p FROM Product p WHERE p.sizeProduct = :sizeProduct")
    Page<Product> findBySizeProduct(@Param("sizeProduct")String sizeProduct, Pageable pageable);
}
