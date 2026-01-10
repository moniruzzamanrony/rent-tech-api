package com.itvillage.renttech.rentalpost;

import com.itvillage.renttech.base.dto.BaseDto;
import com.itvillage.renttech.dynamicform.UserAnswerDFormQuestionRequest;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RentalPostRequest extends BaseDto {
    private String categoryId;
    private List<UserAnswerDFormQuestionRequest> formQuestionsAnswer = new ArrayList<>();

}
