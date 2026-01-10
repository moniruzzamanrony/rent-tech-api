package com.itvillage.renttech.dynamicform;

import lombok.Data;

import java.util.List;

@Data
public class UserAnswerDFormQuestionRequest {
    private String dynamicFormQuestionId;

    private List<String> answers;

}
