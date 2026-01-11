package com.itvillage.renttech.notification;


import lombok.Data;

import java.util.List;

@Data
public class NotificationRequestDto{
    private List<String> receiverIds;

    private String title;

    private String details;

}
