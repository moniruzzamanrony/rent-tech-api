package com.itvillage.renttech.dynamicform;

import com.itvillage.renttech.base.dto.BaseDto;
import lombok.Data;

@Data
public class QuestionOptionResponse extends BaseDto {
    private String name;
    private int value;
    private String iconUrl;
}
