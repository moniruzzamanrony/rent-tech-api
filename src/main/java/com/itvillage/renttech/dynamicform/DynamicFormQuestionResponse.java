package com.itvillage.renttech.dynamicform;

import com.itvillage.renttech.base.dto.BaseDto;
import com.itvillage.renttech.category.Category;
import lombok.Data;

import java.util.List;

@Data

public class DynamicFormQuestionResponse extends BaseDto {

    private Category category;

    private QuestionType questionType;

    private String label;

    private String placeHolder;

    private List<QuestionOptionResponse> defaultOptions;
}
