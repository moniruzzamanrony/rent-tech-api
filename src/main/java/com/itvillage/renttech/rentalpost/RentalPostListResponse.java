package com.itvillage.renttech.rentalpost;

import java.util.List;

public interface RentalPostListResponse {

    String getId();

    String getMobileNo();

    String getCategoryName();

    String getCategoryIconUrl();

    List<AnswerProjection> getAnswers();

    String getTitle();

    int getInterestedPeopleCount();
}
