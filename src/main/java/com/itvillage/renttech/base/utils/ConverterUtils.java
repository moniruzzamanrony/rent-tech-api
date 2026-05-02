package com.itvillage.renttech.base.utils;


import com.itvillage.renttech.base.constants.ApiConstant;
import com.itvillage.renttech.base.modules.s3.UrlCreatorUtils;
import com.itvillage.renttech.category.Category;
import com.itvillage.renttech.category.CategoryResponse;
import com.itvillage.renttech.dynamicform.*;
import com.itvillage.renttech.notification.Notification;
import com.itvillage.renttech.notification.NotificationRequestDto;
import com.itvillage.renttech.notification.NotificationResponseDto;
import com.itvillage.renttech.rentalpost.RentalPost;
import com.itvillage.renttech.rentalpost.RentalPostFile;
import com.itvillage.renttech.rentalpost.RentalPostListResponse;
import com.itvillage.renttech.rentalpost.RentalPostResponse;
import com.itvillage.renttech.verification.user.User;
import com.itvillage.renttech.verification.user.UserPackage;
import com.itvillage.renttech.verification.user.UserPackageResponse;
import com.itvillage.renttech.verification.user.UserResponse;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ConverterUtils {
    public static DynamicFormQuestionResponse convert(DynamicFormQuestion dynamicFormQuestion) {
        DynamicFormQuestionResponse dynamicFormQuestionResponse = new DynamicFormQuestionResponse();
        BeanUtils.copyProperties(dynamicFormQuestion, dynamicFormQuestionResponse, "category");
        dynamicFormQuestionResponse.setCategory(dynamicFormQuestion.getCategory());
        dynamicFormQuestionResponse.setAnswerViewIconUrl(UrlCreatorUtils.buildUrl(dynamicFormQuestion.getAnswerViewIconUrl()));
        if (dynamicFormQuestion.getDefaultOptions() != null) {
            dynamicFormQuestionResponse.setDefaultOptions(dynamicFormQuestion.getDefaultOptions().stream()
                    .map(ConverterUtils::convert)
                    .collect(Collectors.toList()));
        }
        return dynamicFormQuestionResponse;
    }

    public static UserResponse convert(User user, List<String> includes) {
        UserResponse userResponse = new UserResponse();
        BeanUtils.copyProperties(user, userResponse, "userPackages");
        userResponse.setProfilePicUrl(UrlCreatorUtils.buildUrl(user.getProfilePicUrl()));
        if (includes.contains("userPackages") && !user.getUserPackages().isEmpty())
            userResponse.setActivePackage(convert(user.getUserPackages().iterator().next()));
        return userResponse;
    }

    public static UserPackageResponse convert(UserPackage userPackage) {
        UserPackageResponse userPackageResponse = new UserPackageResponse();
        BeanUtils.copyProperties(userPackage, userPackageResponse,  "rentPackage");
        userPackageResponse.setRentPackage(userPackage.getRentPackage());
        return userPackageResponse;
    }

    public static QuestionOptionResponse convert(QuestionOption questionOption) {
        QuestionOptionResponse questionOptionResponse = new QuestionOptionResponse();
        BeanUtils.copyProperties(questionOption, questionOptionResponse);
        questionOptionResponse.setIconUrl(UrlCreatorUtils.buildUrl(questionOption.getIconUrl()));
        return questionOptionResponse;
    }

    public static RentalPostResponse convert(RentalPost rentalPost) {
        RentalPostResponse rentalPostResponse = new RentalPostResponse();
        BeanUtils.copyProperties(rentalPost, rentalPostResponse,"owner","formQuestionsAnswer","rentalPostFiles", "category","interestedPeople");
        rentalPostResponse.setCategory(rentalPost.getCategory());
        rentalPostResponse.setOwner(convert(rentalPost.getOwner(),List.of()));
        rentalPostResponse.setFormQuestionsAnswer(rentalPost.getFormQuestionsAnswer().stream().map(ConverterUtils::convert).collect(Collectors.toList()));
        rentalPostResponse.setRentalPostFiles(rentalPost.getRentalPostFiles().stream().map(ConverterUtils::convertFile).collect(Collectors.toList()));
        rentalPostResponse.setInterestedPeople(rentalPost.getInterestedPeople().stream().map(user -> convert(user,List.of())).collect(Collectors.toSet()));
        return rentalPostResponse;
    }

    public static RentalPostResponse convert(RentalPost rentalPost, List<String> includes) {
        RentalPostResponse rentalPostResponse = new RentalPostResponse();
        BeanUtils.copyProperties(rentalPost, rentalPostResponse,"rentalPostFiles", "category");
        if (includes.contains("category"))
            rentalPostResponse.setCategory(rentalPost.getCategory());
        if (includes.contains("owner"))
            rentalPostResponse.setOwner(convert(rentalPost.getOwner(),List.of()));
        if (includes.contains("formQuestionsAnswer"))
            rentalPostResponse.setFormQuestionsAnswer(rentalPost.getFormQuestionsAnswer().stream().map(ConverterUtils::convert).collect(Collectors.toList()));
        if (includes.contains("rentalPostFiles"))
            rentalPostResponse.setRentalPostFiles(rentalPost.getRentalPostFiles().stream().map(ConverterUtils::convertFile).collect(Collectors.toList()));
        if (includes.contains("interestedPeople"))
            rentalPostResponse.setInterestedPeople(rentalPost.getInterestedPeople().stream().map(user -> convert(user,List.of())).collect(Collectors.toSet()));
        return rentalPostResponse;
    }

    private static RentalPostFile convertFile(RentalPostFile file) {
        RentalPostFile copy = new RentalPostFile();
        BeanUtils.copyProperties(file, copy);
        copy.setUrl(UrlCreatorUtils.buildUrl(file.getUrl()));
        return copy;
    }
//    public static RentalPostListResponse convertToRentalPostListResponse(RentalPost rentalPost) {
//        RentalPostListResponse rentalPostResponse = new RentalPostListResponse();
//        rentalPostResponse.setId(rentalPost.getId());
//        rentalPostResponse.setTitle(
//                rentalPost.getFormQuestionsAnswer().stream()
//                        .filter(ans -> ans.getDynamicFormQuestion().getId().startsWith(ApiConstant.SYS_TITLE_QS_))
//                        .findFirst()
//                        .map(ans -> ans.getAnswers() != null && !ans.getAnswers().isEmpty()
//                                ? new ArrayList<>(ans.getAnswers()).get(0).getAnswer().trim()
//                                : null)
//                        .orElse("N/A")
//        );
//
//        rentalPostResponse.setCategoryName(rentalPost.getCategory().getName());
//        rentalPostResponse.setCategoryIconUrl(rentalPost.getCategory().getIconUrl());
//        rentalPostResponse.setInterestedPeopleCount(rentalPost.getInterestedPeople().size());
//        rentalPostResponse.setMobileNo(rentalPost.getOwner().getMobileNo());
//
//        return rentalPostResponse;
//    }

    public static UserAnswerDFormQuestionResponse convert(UserAnswerDFormQuestion userAnswerDFormQuestion) {
        UserAnswerDFormQuestionResponse userAnswerDFormQuestionResponse = new UserAnswerDFormQuestionResponse();
        BeanUtils.copyProperties(userAnswerDFormQuestion, userAnswerDFormQuestionResponse, "answers");
        userAnswerDFormQuestionResponse.setQuestion(convert(userAnswerDFormQuestion.getDynamicFormQuestion()));
        userAnswerDFormQuestionResponse.setAnswers(userAnswerDFormQuestion.getAnswersAsStrings());
        return userAnswerDFormQuestionResponse;
    }

    public static NotificationResponseDto convert(Notification notification) {
        NotificationResponseDto notificationResponseDto = new NotificationResponseDto();
        BeanUtils.copyProperties(notification, notificationResponseDto);
        notificationResponseDto.setSender(ConverterUtils.convert(notification.getSender(),List.of()));
        notificationResponseDto.setReceiver(ConverterUtils.convert(notification.getReceiver(),List.of()));
        return notificationResponseDto;
    }

    public static NotificationRequestDto createNotificationRequestDto(List<String> receiverIds, String title, String details) {
        NotificationRequestDto notificationDto = new NotificationRequestDto();
        notificationDto.setReceiverIds(receiverIds);
        notificationDto.setTitle(title);
        notificationDto.setDetails(details);
        return notificationDto;
    }

    public static CategoryResponse convert(Category category) {
        CategoryResponse categoryResponse = new CategoryResponse();
        BeanUtils.copyProperties(category, categoryResponse);
        categoryResponse.setIconUrl(UrlCreatorUtils.buildUrl(category.getIconUrl()));
        return categoryResponse;
    }
}
