package com.itvillage.renttech.dynamicform;


import com.itvillage.renttech.base.constants.ApiConstant;
import com.itvillage.renttech.base.dto.APIResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiConstant.PRIVATE_BASE_API + "/dynamic-form")
@RequiredArgsConstructor
public class DynamicFormController {

    private final DynamicFormService dynamicFormService;

    @PostMapping()
    public APIResponseDto<DynamicFormQuestionResponse> createDynamicFormQuestion(@RequestBody DynamicFormQuestionRequest request) {
        return new APIResponseDto<>(HttpStatus.OK.value(), dynamicFormService.createDynamicFormQuestion(request));
    }

    @PostMapping("/questions/{questionId}/options")
    public APIResponseDto<DynamicFormQuestionResponse> addOptionsInQuestion(@RequestBody QuestionOptionRequest request, @PathVariable String questionId) {
        return new APIResponseDto<>(HttpStatus.OK.value(), dynamicFormService.addOptionsInQuestion(questionId, request));
    }

    @DeleteMapping("/questions/{questionId}")
    public void deleteQuestion(@PathVariable String questionId) {
        dynamicFormService.deleteQuestion(questionId);
    }

    @DeleteMapping("/questions/{questionId}/options/{optionId}")
    public void deleteOptionOfQuestion(@PathVariable String questionId, @PathVariable String optionId) {
        dynamicFormService.deleteOptionOfQuestion(questionId, optionId);
    }

    @GetMapping("/categories/{categoryId}/form")
    public APIResponseDto<List<DynamicFormQuestionResponse>> getFormBycCategoryId(@PathVariable String categoryId) {
        return new APIResponseDto<>(HttpStatus.OK.value(), dynamicFormService.getFormBycCategoryId(categoryId));
    }
}
