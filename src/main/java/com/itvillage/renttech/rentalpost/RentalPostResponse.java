package com.itvillage.renttech.rentalpost;

import com.itvillage.renttech.base.dto.BaseDto;
import com.itvillage.renttech.category.Category;
import com.itvillage.renttech.dynamicform.UserAnswerDFormQuestionResponse;
import com.itvillage.renttech.verification.user.UserResponse;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class RentalPostResponse extends BaseDto {

    private UserResponse owner;

    private Category category;


    private double latitude;

    private double longitude;


    private List<RentalPostFile> rentalPostFiles = new ArrayList<>();


    private List<UserAnswerDFormQuestionResponse> formQuestionsAnswer = new ArrayList<>();


    private Set<UserResponse> interestedPeople = new HashSet<>();
}
