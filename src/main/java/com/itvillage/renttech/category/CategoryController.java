package com.itvillage.renttech.category;



import com.itvillage.renttech.base.constants.ApiConstant;
import com.itvillage.renttech.base.controller.MagicController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConstant.PRIVATE_BASE_API+"/category")
public class CategoryController extends MagicController<CategoryService, Category> {
    public CategoryController(CategoryService service) {
        super(service);
    }
}
