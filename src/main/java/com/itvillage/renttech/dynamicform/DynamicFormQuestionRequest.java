package com.itvillage.renttech.dynamicform;

import lombok.Data;

@Data
public class DynamicFormQuestionRequest {

    private String id;

    private String categoryId;

    private PurposeType purposeType;

    private QuestionType questionType;

    private String label;

    private String placeHolder;

    private boolean qsRequired;

    private InputType inputType;

    private int position;
}
