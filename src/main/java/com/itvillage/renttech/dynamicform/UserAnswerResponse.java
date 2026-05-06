package com.itvillage.renttech.dynamicform;

import lombok.Data;

@Data
public class UserAnswerResponse {
    private QuestionOptionResponse option;
    private String value;
}
