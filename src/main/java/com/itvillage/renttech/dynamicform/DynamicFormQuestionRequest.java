package com.itvillage.renttech.dynamicform;

import lombok.Data;

@Data
public class DynamicFormQuestionRequest {

    private String categoryId;

    private QuestionType questionType;

    private String label;

    private String placeHolder;

    private InputType inputType;

    private int position;
}
