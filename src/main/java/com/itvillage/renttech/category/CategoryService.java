package com.itvillage.renttech.category;

import com.itvillage.renttech.base.constants.ApiConstant;
import com.itvillage.renttech.base.modules.s3.SpaceService;
import com.itvillage.renttech.base.service.MagicService;
import com.itvillage.renttech.base.utils.ConverterUtils;
import com.itvillage.renttech.dynamicform.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService extends MagicService<Category, String> {

    private final CategoryRepository categoryRepository;
    private final DynamicFormService dynamicFormService;
    private final SpaceService spaceService;

    public CategoryService(
            CategoryRepository repository,
            DynamicFormService dynamicFormService,
            SpaceService spaceService
    ) {
        super(repository);
        this.categoryRepository = repository;
        this.dynamicFormService = dynamicFormService;
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

        createSystemDynamicQuestions(category.getId());

        return ConverterUtils.convert(category);
    }

    private void createSystemDynamicQuestions(String categoryId) {

        // PRICE QUESTION
        DynamicFormQuestionRequest priceDQ = new DynamicFormQuestionRequest();
        priceDQ.setId(ApiConstant.SYS_PRICE_QS_ + categoryId.substring(4));
        priceDQ.setCategoryId(categoryId);
        priceDQ.setQuestionType(QuestionType.INPUT);
        priceDQ.setLabel("Price");
        priceDQ.setPurposeType(PurposeType.OTHERS);
        priceDQ.setPlaceHolder("Enter your price");
        priceDQ.setInputType(InputType.DECIMAL);
        priceDQ.setPosition(1);
        priceDQ.setQsRequired(true);

        dynamicFormService.createDynamicFormQuestion(priceDQ, null);

        // LOCATION QUESTION
        DynamicFormQuestionRequest locationDQ = new DynamicFormQuestionRequest();
        locationDQ.setId(ApiConstant.SYS_LOCATION_QS_ + categoryId.substring(4));
        locationDQ.setCategoryId(categoryId);
        locationDQ.setQuestionType(QuestionType.INPUT);
        locationDQ.setLabel("Location");
        locationDQ.setPurposeType(PurposeType.OTHERS);
        locationDQ.setPlaceHolder("Enter lat long");
        locationDQ.setInputType(InputType.TEXT);
        locationDQ.setPosition(2);
        locationDQ.setQsRequired(true);
        dynamicFormService.createDynamicFormQuestion(locationDQ, null);

        // POST TITLE QUESTION
        DynamicFormQuestionRequest postTitle = new DynamicFormQuestionRequest();
        postTitle.setId(ApiConstant.SYS_TITLE_QS_ + categoryId.substring(4));
        postTitle.setCategoryId(categoryId);
        postTitle.setQuestionType(QuestionType.INPUT);
        postTitle.setLabel("Post Title");
        postTitle.setPurposeType(PurposeType.OTHERS);
        postTitle.setPlaceHolder("Enter Post Title");
        postTitle.setInputType(InputType.TEXT);
        postTitle.setPosition(3);
        postTitle.setQsRequired(true);
        dynamicFormService.createDynamicFormQuestion(postTitle, null);

        // POST TITLE QUESTION
        DynamicFormQuestionRequest addressQs = new DynamicFormQuestionRequest();
        addressQs.setId(ApiConstant.SYS_ADDRESS_QS_ + categoryId.substring(4));
        addressQs.setCategoryId(categoryId);
        addressQs.setQuestionType(QuestionType.INPUT);
        addressQs.setLabel("Address");
        addressQs.setPurposeType(PurposeType.OTHERS);
        addressQs.setPlaceHolder("Enter Address");
        addressQs.setInputType(InputType.TEXT);
        addressQs.setPosition(4);
        addressQs.setQsRequired(true);
        dynamicFormService.createDynamicFormQuestion(addressQs, null);

        // POST TITLE QUESTION
        DynamicFormQuestionRequest availableFromQs = new DynamicFormQuestionRequest();
        availableFromQs.setId(ApiConstant.SYS_AVAILABLE_FROM_QS_ + categoryId.substring(4));
        availableFromQs.setCategoryId(categoryId);
        availableFromQs.setQuestionType(QuestionType.INPUT);
        availableFromQs.setLabel("Available From");
        availableFromQs.setPurposeType(PurposeType.OTHERS);
        availableFromQs.setPlaceHolder("Enter Available Date");
        availableFromQs.setInputType(InputType.DATE);
        availableFromQs.setPosition(5);
        availableFromQs.setQsRequired(true);
        dynamicFormService.createDynamicFormQuestion(availableFromQs, null);
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
}
