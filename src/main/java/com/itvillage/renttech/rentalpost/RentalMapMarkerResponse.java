package com.itvillage.renttech.rentalpost;

import com.itvillage.renttech.dynamicform.UserAnswerDFormQuestionResponse;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RentalMapMarkerResponse {
    private String id;

    private String name;

    private String availableFrom;

    private String price;

    private String address;

    private String division;

    private String zilla;

    private String thanaOrUpazila;

    private double latitude;

    private double longitude;

    private boolean valid;

    // first 3 SPECIFICATION question answers
    private List<UserAnswerDFormQuestionResponse> first3Specifications = new ArrayList<>();
}
