package com.itvillage.renttech.dynamicform;

import jakarta.persistence.Column;
import lombok.Data;

import java.util.List;

@Data
public class UserAnswerDFormQuestionRequest {
    private String dynamicFormQuestionId;

    @Column(nullable = false)
    private List<String> answer;

}
