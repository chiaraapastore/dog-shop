package dogshop.market.service;


import dogshop.market.entity.Category;
import dogshop.market.entity.Product;
import dogshop.market.repository.CategoryRepository;
import dogshop.market.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UtilityService {
    CategoryRepository catrep;
    ProductRepository prodrep;

    public UtilityService(CategoryRepository catrep, ProductRepository prodrep) {
        this.catrep = catrep;
        this.prodrep = prodrep;
    }

    @Transactional(rollbackFor = Exception.class)
    public void populate(){
        Category c = new Category();
        c.setCategoryName("Accessori");
        catrep.save(c);

        c = new Category();
        c.setCategoryName("Alimenti");
        catrep.save(c);

        int id =0;
        for(Category cat : catrep.findAll()){
            for (int i=0;i<5;i++){
                Product p = new Product();
                p.setCategoryName("prodotto"+id++);
                p.setCategory(cat);
                prodrep.save(p);
            }
        }
    }
}