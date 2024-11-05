package dogshop.market.service;


import dogshop.market.entity.Category;
import dogshop.market.repository.CategoryRepository;
import dogshop.market.repository.UtenteShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
    public class CategoryService {

        private final CategoryRepository categoryRepository;

        @Autowired
        public CategoryService(CategoryRepository categoryRepository) {
            this.categoryRepository = categoryRepository;
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



}
