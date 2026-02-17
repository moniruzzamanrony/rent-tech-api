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

    private boolean qsRequired;

    private String placeHolder;

    private String answerViewIconUrl;

    private InputType inputType;

    private int position;

    private List<QuestionOptionResponse> defaultOptions;
}
