package com.itvillage.renttech.rentalpost;

import com.itvillage.renttech.base.dto.BaseDto;
import lombok.Data;

@Data
public class RentalPostListResponse extends BaseDto {
    private String categoryName;
    private String categoryIconUrl;
    private String title;
    private int interestedPeopleCount;
}
