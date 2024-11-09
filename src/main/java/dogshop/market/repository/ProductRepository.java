package dogshop.market.repository;
import dogshop.market.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByCategory_CategoryName(String categoryName, Pageable pageable);
    Page<Product> findBySizeProduct(String sizeProduct, Pageable pageable);
}
