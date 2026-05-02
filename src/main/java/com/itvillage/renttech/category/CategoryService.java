package com.itvillage.renttech.category;

import com.itvillage.renttech.base.modules.s3.SpaceService;
import com.itvillage.renttech.base.service.MagicService;
import com.itvillage.renttech.base.utils.ConverterUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService extends MagicService<Category, String> {

    private final CategoryRepository categoryRepository;
    private final CategoryAsyncService categoryAsyncService;
    private final SpaceService spaceService;

    public CategoryService(
            CategoryRepository repository,
            CategoryAsyncService categoryAsyncService,
            SpaceService spaceService
    ) {
        super(repository);
        this.categoryRepository = repository;
        this.categoryAsyncService = categoryAsyncService;
        this.spaceService = spaceService;
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

        categoryAsyncService.createSystemDynamicQuestions(category.getId());

        return ConverterUtils.convert(category);
    }

    public CategoryResponse categoryActiveInActive(String catId, boolean active) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        category.setActive(active);
        category = categoryRepository.save(category);

        return  ConverterUtils.convert(category);
    }

    public List<CategoryResponse> getAllActiveCat() {
        List<Category> categories = categoryRepository.findByActiveTrue();
        return categories.stream().map(ConverterUtils::convert).collect(Collectors.toList());
    }

    public List<CategoryResponse> getAllCat() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream().map(ConverterUtils::convert).collect(Collectors.toList());
    }
}
