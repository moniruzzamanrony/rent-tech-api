package com.itvillage.renttech.dynamicform;

import com.itvillage.renttech.base.dto.BaseDto;
import lombok.Data;

import java.util.List;

@Data
public class UserAnswerDFormQuestionResponse extends BaseDto {
    private DynamicFormQuestionResponse question;

    private List<String> answers;

}
