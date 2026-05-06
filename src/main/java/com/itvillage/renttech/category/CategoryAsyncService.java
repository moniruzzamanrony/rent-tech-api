package com.itvillage.renttech.category;

import com.itvillage.renttech.base.constants.ApiConstant;
import com.itvillage.renttech.dynamicform.*;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryAsyncService {

    private final DynamicFormService dynamicFormService;

    @Async
    public void createSystemDynamicQuestions(String categoryId) {

        DynamicFormQuestionRequest priceDQ = new DynamicFormQuestionRequest();
        priceDQ.setId(ApiConstant.SYS_PRICE_QS_ + categoryId.substring(4));
        priceDQ.setCategoryId(categoryId);
        priceDQ.setQuestionType(QuestionType.INPUT);
        priceDQ.setLabel("Price");
        priceDQ.setPurposeType(PurposeType.OTHERS);
        priceDQ.setPlaceHolder("Enter your price");
        priceDQ.setInputType(InputType.DECIMAL);
        priceDQ.setPosition(1);
        priceDQ.setQsRequired(true);
        dynamicFormService.createDynamicFormQuestion(priceDQ, null);

        DynamicFormQuestionRequest locationDQ = new DynamicFormQuestionRequest();
        locationDQ.setId(ApiConstant.SYS_LOCATION_QS_ + categoryId.substring(4));
        locationDQ.setCategoryId(categoryId);
        locationDQ.setQuestionType(QuestionType.INPUT);
        locationDQ.setLabel("Location");
        locationDQ.setPurposeType(PurposeType.OTHERS);
        locationDQ.setPlaceHolder("Enter lat long");
        locationDQ.setInputType(InputType.TEXT);
        locationDQ.setPosition(2);
        locationDQ.setQsRequired(true);
        dynamicFormService.createDynamicFormQuestion(locationDQ, null);

        DynamicFormQuestionRequest postTitle = new DynamicFormQuestionRequest();
        postTitle.setId(ApiConstant.SYS_TITLE_QS_ + categoryId.substring(4));
        postTitle.setCategoryId(categoryId);
        postTitle.setQuestionType(QuestionType.INPUT);
        postTitle.setLabel("Post Title");
        postTitle.setPurposeType(PurposeType.OTHERS);
        postTitle.setPlaceHolder("Enter Post Title");
        postTitle.setInputType(InputType.TEXT);
        postTitle.setPosition(3);
        postTitle.setQsRequired(true);
        dynamicFormService.createDynamicFormQuestion(postTitle, null);

        DynamicFormQuestionRequest availableFromQs = new DynamicFormQuestionRequest();
        availableFromQs.setId(ApiConstant.SYS_AVAILABLE_FROM_QS_ + categoryId.substring(4));
        availableFromQs.setCategoryId(categoryId);
        availableFromQs.setQuestionType(QuestionType.INPUT);
        availableFromQs.setLabel("Available From");
        availableFromQs.setPurposeType(PurposeType.OTHERS);
        availableFromQs.setPlaceHolder("Enter Available Date");
        availableFromQs.setInputType(InputType.DATE);
        availableFromQs.setPosition(5);
        availableFromQs.setQsRequired(true);
        dynamicFormService.createDynamicFormQuestion(availableFromQs, null);
    }
}
