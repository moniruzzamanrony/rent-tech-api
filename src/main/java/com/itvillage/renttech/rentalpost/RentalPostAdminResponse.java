package com.itvillage.renttech.rentalpost;

import com.itvillage.renttech.base.dto.BaseDto;
import com.itvillage.renttech.category.Category;
import com.itvillage.renttech.dynamicform.UserAnswerDFormQuestionResponse;
import com.itvillage.renttech.verification.user.UserResponse;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class RentalPostAdminResponse extends BaseDto {

    private String ownerName;

    private String ownerPhoneNo;

    private String categoryName;

    private double latitude;

    private double longitude;

    private String address;

    private String division;

    private String zilla;

    private String thanaOrUpazila;

    private String price;

    private int countInterestedPeople;

    private ZonedDateTime expiryDate;
}
