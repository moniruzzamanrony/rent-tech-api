package com.itvillage.renttech.rentalpost;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.itvillage.renttech.base.modules.s3.UrlCreatorUtils;

import java.util.List;

public interface RentalPostListResponse {

    String getId();

    String getMobileNo();

    String getCategoryName();

    @JsonIgnore
    String getCategoryIconUrl();

    @JsonProperty("categoryIconUrl")
    default String getResolvedCategoryIconUrl() {
        return UrlCreatorUtils.buildUrl(getCategoryIconUrl());
    }

    List<AnswerProjection> getAnswers();

    String getTitle();

    int getInterestedPeopleCount();
}
