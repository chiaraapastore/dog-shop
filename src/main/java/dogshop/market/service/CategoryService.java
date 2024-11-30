package dogshop.market.service;


import dogshop.market.entity.Category;
import dogshop.market.repository.CategoryRepository;
import dogshop.market.repository.ProductRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    public Category findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    public List<Category> findAllCategories() {
        return categoryRepository.findAll();
    }

    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    public Category updateCategory(Long id, Category categoryDetails) {
        Optional<Category> existingCategory = categoryRepository.findById(id);
        if (existingCategory.isPresent()) {
            Category category = existingCategory.get();
            category.setCategoryName(categoryDetails.getCategoryName());
            category.setCategoryDescription(categoryDetails.getCategoryDescription());
            return categoryRepository.save(category);
        }
        return null;
    }

    public boolean deleteCategory(Long id) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Scheduled(cron = "0 0/30 * * * *")
    public void syncCountProduct() {
        List<Category> categories = categoryRepository.findAll();
        for (Category category : categories) {
            int actualProductCount = productRepository.countByCategory(category);
            category.setCountProduct(actualProductCount);
            categoryRepository.save(category);
        }
        System.out.println("Sincronizzazione automatica del countProduct completata.");
    }
}
