package com.itvillage.renttech.dynamicform;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itvillage.renttech.base.constants.ApiConstant;
import com.itvillage.renttech.base.dto.APIResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(ApiConstant.PRIVATE_BASE_API + "/dynamic-form")
@RequiredArgsConstructor
public class DynamicFormController {

    private final DynamicFormService dynamicFormService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public APIResponseDto<DynamicFormQuestionResponse> createDynamicFormQuestion(
            @RequestPart("request") String requestString,
            @RequestPart(value = "file", required = false) MultipartFile file) throws JsonProcessingException {
        // Convert JSON string to object
        ObjectMapper mapper = new ObjectMapper();
        DynamicFormQuestionRequest request = mapper.readValue(requestString, DynamicFormQuestionRequest.class);

        return new APIResponseDto<>(HttpStatus.OK.value(),
                dynamicFormService.createDynamicFormQuestion(request, file));
    }
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public APIResponseDto<DynamicFormQuestionResponse> updateDynamicFormQuestion(
            @PathVariable String id,
            @RequestPart("request") String requestString,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        DynamicFormQuestionRequest request =
                objectMapper.readValue(requestString, DynamicFormQuestionRequest.class);

        return new APIResponseDto<>(
                HttpStatus.OK.value(),
                dynamicFormService.updateDynamicFormQuestion(id, request, file)
        );
    }

    @PostMapping(
            value = "/questions/{questionId}/options",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public APIResponseDto<DynamicFormQuestionResponse> addOptionsInQuestion(
            @PathVariable String questionId,
            @RequestPart("data") String requestString,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        QuestionOptionRequest request = mapper.readValue(requestString, QuestionOptionRequest.class);
        DynamicFormQuestionResponse response =
                dynamicFormService.addOptionsInQuestion(questionId, request, file);
        return new APIResponseDto<>(HttpStatus.OK.value(), response);
    }

    @PutMapping(
            value = "/questions/{questionId}/options/{optionId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public APIResponseDto<DynamicFormQuestionResponse> updateOptionInQuestion(
            @PathVariable String questionId,
            @PathVariable String optionId,
            @RequestPart("data") String requestString,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        QuestionOptionRequest request =
                objectMapper.readValue(requestString, QuestionOptionRequest.class);

        DynamicFormQuestionResponse response =
                dynamicFormService.updateOptionInQuestion(questionId, optionId, request, file);

        return new APIResponseDto<>(HttpStatus.OK.value(), response);
    }

    @PostMapping( "/questions/position/update")
    public APIResponseDto<List<DynamicFormQuestionResponse>> updatePositionOfDynamicQs(
            @RequestBody List<DynamicFormQuestionRequest> dynamicFormQuestionRequests) {

        List<DynamicFormQuestionResponse> response =
                dynamicFormService.updatePositionOfDynamicQs(dynamicFormQuestionRequests);
        return new APIResponseDto<>(HttpStatus.OK.value(), response);
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
