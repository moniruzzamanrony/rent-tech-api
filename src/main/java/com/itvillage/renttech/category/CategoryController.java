package com.itvillage.renttech.category;



import com.itvillage.renttech.base.constants.ApiConstant;
import com.itvillage.renttech.base.controller.MagicController;
import com.itvillage.renttech.base.dto.APIResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(ApiConstant.PRIVATE_BASE_API+"/category")
public class CategoryController extends MagicController<CategoryService, Category> {
    private CategoryService categoryService;
    public CategoryController(CategoryService service) {
        super(service);
        categoryService = service;
    }

    ///  wrrite a create api with multipart
    @PostMapping(path = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public APIResponseDto<CategoryResponse> createCategory(
            @RequestPart("request") CategoryRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        return new APIResponseDto<>(HttpStatus.OK.value(), categoryService.createCategory(request, file));
    }

}
