package com.itvillage.renttech.base.utils;


import com.itvillage.renttech.dynamicform.DynamicFormQuestion;
import com.itvillage.renttech.dynamicform.DynamicFormQuestionResponse;
import com.itvillage.renttech.dynamicform.QuestionOption;
import com.itvillage.renttech.dynamicform.QuestionOptionResponse;
import com.itvillage.renttech.verification.user.User;
import com.itvillage.renttech.verification.user.UserResponse;
import org.springframework.beans.BeanUtils;

import java.util.stream.Collectors;

public class ConverterUtils {
    public static DynamicFormQuestionResponse convert(DynamicFormQuestion dynamicFormQuestion) {
        DynamicFormQuestionResponse dynamicFormQuestionResponse = new DynamicFormQuestionResponse();
        BeanUtils.copyProperties(dynamicFormQuestion, dynamicFormQuestionResponse, "category");
        dynamicFormQuestionResponse.setCategory(dynamicFormQuestion.getCategory());
        if (dynamicFormQuestion.getDefaultOptions() != null) {
            dynamicFormQuestionResponse.setDefaultOptions(dynamicFormQuestion.getDefaultOptions().stream()
                    .map(ConverterUtils::convert)
                    .collect(Collectors.toList()));
        }
        return dynamicFormQuestionResponse;
    }

    public static UserResponse convert(User user) {
        UserResponse userResponse = new UserResponse();
        BeanUtils.copyProperties(user, userResponse);
        return userResponse;
    }
    public static QuestionOptionResponse convert(QuestionOption questionOption) {
        QuestionOptionResponse questionOptionResponse = new QuestionOptionResponse();
        BeanUtils.copyProperties(questionOption, questionOptionResponse);
        return questionOptionResponse;
    }


//    public static TravelPlanResponseDto convert(TravelPlan travelPlan) {
//        if(travelPlan ==  null) return null;
//        TravelPlanResponseDto travelPlanResponseDto = new TravelPlanResponseDto();
//        BeanUtils.copyProperties(travelPlan, travelPlanResponseDto);
//        travelPlanResponseDto.setCountries(travelPlan.getCountries());
//        travelPlanResponseDto.setOwner(convert(travelPlan.getOwner()));
//        return travelPlanResponseDto;
//    }
//
//    public static TravelPlanResponseDto convertWithBids(TravelPlan travelPlan, List<Bid> bids) {
//        TravelPlanResponseDto travelPlanResponseDto = convert(travelPlan);
//        travelPlanResponseDto.setBids(bids.stream().map(ConverterUtils::convert).collect(Collectors.toList()));
//        return travelPlanResponseDto;
//    }
//
//    public static TravelPlanResponseDto convertWithLazyBids(TravelPlan travelPlan, List<Bid> bids) {
//        TravelPlanResponseDto travelPlanResponseDto = convert(travelPlan);
//        travelPlanResponseDto.setBids(bids.stream().map(bid -> {
//            BidResponseDto bidResponseDto = new BidResponseDto();
//            bidResponseDto.setId(bid.getId());
//            return bidResponseDto;
//        }).collect(Collectors.toList()));
//        return travelPlanResponseDto;
//    }
//
//    public static BidResponseDto convert(Bid bid) {
//        BidResponseDto bidResponseDto = new BidResponseDto();
//        BeanUtils.copyProperties(bid, bidResponseDto);
//        bidResponseDto.setBidder(convert(bid.getBidder()));
//        return bidResponseDto;
//    }
//
//    public static NotificationDto convert(Notification notification) {
//        NotificationDto notificationDto = new NotificationDto();
//        BeanUtils.copyProperties(notification, notificationDto);
//        return notificationDto;
//    }
//
//    public static NotificationRequestDto createNotificationRequestDto(List<String> receiverIds, String title, String details) {
//        NotificationRequestDto notificationDto = new NotificationRequestDto();
//        notificationDto.setReceiverIds(receiverIds);
//        notificationDto.setTitle(title);
//        notificationDto.setDetails(details);
//        return notificationDto;
//    }
//
//    public static CoinHistoryResponse convert(CoinHistory coinHistory) {
//        CoinHistoryResponse coinHistoryResponse = new CoinHistoryResponse();
//        BeanUtils.copyProperties(coinHistory, coinHistoryResponse);
//        coinHistoryResponse.setUser(convert(coinHistory.getUser()));
//        return coinHistoryResponse;
//    }
}
