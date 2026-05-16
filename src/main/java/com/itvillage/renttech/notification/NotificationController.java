package com.itvillage.renttech.notification;



import com.itvillage.renttech.base.constants.ApiConstant;
import com.itvillage.renttech.base.dto.APIResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping(ApiConstant.PRIVATE_BASE_API)
public class NotificationController {
    private final NotificationService notificationService;


    @GetMapping("/notifications")
    public APIResponseDto<Page<NotificationResponseDto>> getLoggedUserNotifications(Pageable pageable) {
        Page<NotificationResponseDto> loggedUserNotifications = notificationService.getLoggedUserNotifications(pageable);
        return new APIResponseDto<>(HttpStatus.OK.value(), loggedUserNotifications);
    }

    @GetMapping("/notifications/unseen-count")
    public APIResponseDto<Integer> getLoggedUserUnseenNotificationsCount() {
        Integer count = notificationService.getLoggedUserUnseenNotificationsCount();
        return new APIResponseDto<>(HttpStatus.OK.value(), count);
    }
}
