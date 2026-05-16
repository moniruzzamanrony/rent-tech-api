package com.itvillage.renttech.category;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itvillage.renttech.base.constants.ApiConstant;
import com.itvillage.renttech.base.controller.MagicController;
import com.itvillage.renttech.base.dto.APIResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
            @RequestPart("request") String requestString,
            @RequestPart(value = "file", required = false) MultipartFile file) throws JsonProcessingException {
        // Convert JSON string to object
        ObjectMapper mapper = new ObjectMapper();
        CategoryRequest request = mapper.readValue(requestString, CategoryRequest.class);

        return new APIResponseDto<>(HttpStatus.OK.value(), categoryService.createCategory(request, file));
    }

    @PostMapping("/{catId}/active-inactive")
    public APIResponseDto<CategoryResponse> categoryActiveInActive(@PathVariable String catId, @RequestParam boolean active) {
        return new APIResponseDto<>(HttpStatus.OK.value(), categoryService.categoryActiveInActive(catId, active));
    }

    // ✅ NEW ENDPOINT TO FETCH DYNAMIC FORM QUESTIONS FOR A CATEGORY
    @GetMapping("/all/active")
    public APIResponseDto<List<CategoryResponse>> getAllActiveCat() {
        return new APIResponseDto<>(HttpStatus.OK.value(), categoryService.getAllActiveCat());
    }

    @GetMapping("/all")
    public List<CategoryResponse> getAllCat() {
        return categoryService.getAllCat();
    }

}
