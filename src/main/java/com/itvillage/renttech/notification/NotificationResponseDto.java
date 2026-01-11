package com.itvillage.renttech.notification;



import com.itvillage.renttech.base.dto.BaseDto;
import com.itvillage.renttech.verification.user.User;
import lombok.Data;

@Data
public class NotificationResponseDto extends BaseDto {
    private User sender;

    private User receiver;

    private String title;

    private String details;

    private boolean seen;
}
