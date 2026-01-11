package com.itvillage.renttech.notification;


import com.itvillage.renttech.base.dto.BaseDto;
import com.itvillage.renttech.verification.user.UserResponse;
import lombok.Data;

@Data
public class NotificationResponseDto extends BaseDto {
    private UserResponse sender;

    private UserResponse receiver;

    private String title;

    private String details;

    private boolean seen;
}
