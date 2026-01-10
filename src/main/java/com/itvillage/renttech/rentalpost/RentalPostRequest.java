package com.itvillage.renttech.rentalpost;

import com.itvillage.renttech.dynamicform.UserAnswerDFormQuestionRequest;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RentalPostRequest {
    private String categoryId;
    private List<UserAnswerDFormQuestionRequest> formQuestionsAnswer = new ArrayList<>();

}
