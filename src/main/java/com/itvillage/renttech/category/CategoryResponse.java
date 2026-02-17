package com.itvillage.renttech.category;

import com.itvillage.renttech.base.dto.BaseDto;
import lombok.Data;

@Data
public class CategoryResponse extends BaseDto {
    private String name;
    private String iconUrl;
    private String description;
}
