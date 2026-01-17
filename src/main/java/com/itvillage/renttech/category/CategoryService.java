package com.itvillage.renttech.category;



import com.itvillage.renttech.base.modules.s3.SpaceService;
import com.itvillage.renttech.base.service.MagicService;
import com.itvillage.renttech.base.utils.ConverterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CategoryService extends MagicService<Category, String> {
    private CategoryRepository categoryRepository;
    @Autowired
    private SpaceService spaceService;
    public CategoryService(CategoryRepository repository) {
        super(repository);
        categoryRepository = repository;
    }

    public CategoryResponse createCategory(CategoryRequest request, MultipartFile file) {
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());

         if (file != null && !file.isEmpty()) {
             String iconUrl = spaceService.uploadFile(file);
             category.setIconUrl(iconUrl);
         }
        category = categoryRepository.save(category);


        return ConverterUtils.convert(category);
    }
}
