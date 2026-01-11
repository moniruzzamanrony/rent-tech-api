package com.itvillage.renttech.base.utils;


import com.itvillage.renttech.dynamicform.*;
import com.itvillage.renttech.notification.Notification;
import com.itvillage.renttech.notification.NotificationRequestDto;
import com.itvillage.renttech.notification.NotificationResponseDto;
import com.itvillage.renttech.rentalpost.RentalPost;
import com.itvillage.renttech.rentalpost.RentalPostResponse;
import com.itvillage.renttech.verification.user.User;
import com.itvillage.renttech.verification.user.UserResponse;
import org.springframework.beans.BeanUtils;

import java.util.List;
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

    public static RentalPostResponse convert(RentalPost rentalPost) {
        RentalPostResponse rentalPostResponse = new RentalPostResponse();
        BeanUtils.copyProperties(rentalPost, rentalPostResponse,"rentalPostFiles", "category");
        rentalPostResponse.setCategory(rentalPost.getCategory());
        rentalPostResponse.setOwner(convert(rentalPost.getOwner()));
        rentalPostResponse.setFormQuestionsAnswer(rentalPost.getFormQuestionsAnswer().stream().map(ConverterUtils::convert).collect(Collectors.toList()));
        rentalPostResponse.setRentalPostFiles(rentalPost.getRentalPostFiles());
        rentalPostResponse.setInterestedPeople(rentalPost.getInterestedPeople().stream().map(ConverterUtils::convert).collect(Collectors.toSet()));
        return rentalPostResponse;
    }

    public static RentalPostResponse convert(RentalPost rentalPost, List<String> includes) {
        RentalPostResponse rentalPostResponse = new RentalPostResponse();
        BeanUtils.copyProperties(rentalPost, rentalPostResponse,"rentalPostFiles", "category");
        if (includes.contains("category"))
            rentalPostResponse.setCategory(rentalPost.getCategory());
        if (includes.contains("owner"))
            rentalPostResponse.setOwner(convert(rentalPost.getOwner()));
        if (includes.contains("formQuestionsAnswer"))
            rentalPostResponse.setFormQuestionsAnswer(rentalPost.getFormQuestionsAnswer().stream().map(ConverterUtils::convert).collect(Collectors.toList()));
        if (includes.contains("rentalPostFiles"))
            rentalPostResponse.setRentalPostFiles(rentalPost.getRentalPostFiles());
        if (includes.contains("interestedPeople"))
            rentalPostResponse.setInterestedPeople(rentalPost.getInterestedPeople().stream().map(ConverterUtils::convert).collect(Collectors.toSet()));
        return rentalPostResponse;
    }

    private static UserAnswerDFormQuestionResponse convert(UserAnswerDFormQuestion userAnswerDFormQuestion) {
        UserAnswerDFormQuestionResponse userAnswerDFormQuestionResponse = new UserAnswerDFormQuestionResponse();
        BeanUtils.copyProperties(userAnswerDFormQuestion, userAnswerDFormQuestionResponse);
        userAnswerDFormQuestionResponse.setQuestion(convert(userAnswerDFormQuestion.getDynamicFormQuestion()));
        return userAnswerDFormQuestionResponse;
    }

    public static NotificationResponseDto convert(Notification notification) {
        NotificationResponseDto notificationResponseDto = new NotificationResponseDto();
        BeanUtils.copyProperties(notification, notificationResponseDto);
        notificationResponseDto.setSender(notification.getSender());
        notificationResponseDto.setReceiver(notification.getReceiver());
        return notificationResponseDto;
    }

    public static NotificationRequestDto createNotificationRequestDto(List<String> receiverIds, String title, String details) {
        NotificationRequestDto notificationDto = new NotificationRequestDto();
        notificationDto.setReceiverIds(receiverIds);
        notificationDto.setTitle(title);
        notificationDto.setDetails(details);
        return notificationDto;
    }
}
