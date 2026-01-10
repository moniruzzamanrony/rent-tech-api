package com.itvillage.renttech.category;



import com.itvillage.renttech.base.service.MagicService;
import org.springframework.stereotype.Service;

@Service
public class CategoryService extends MagicService<Category, String> {
    public CategoryService(CategoryRepository repository) {
        super(repository);
    }
}
