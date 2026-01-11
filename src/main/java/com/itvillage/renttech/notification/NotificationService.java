package com.itvillage.renttech.notification;


import com.itvillage.renttech.base.utils.ConverterUtils;
import com.itvillage.renttech.base.utils.TokenUtils;
import com.itvillage.renttech.verification.user.User;
import com.itvillage.renttech.verification.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
  private final NotificationRepository notificationRepository;
  private final UserService userService;

  public List<NotificationResponseDto> getLoggedUserNotifications() {
    List<Notification> notifications =
        notificationRepository.findAllByReceiverIdOrderByCreatedDateDesc(TokenUtils.getCurrentUserId());
    List<NotificationResponseDto> notificationResponseDtos = notifications.stream().map(ConverterUtils::convert).toList();
    markAsView(notifications);
    return notificationResponseDtos;
  }

    private void markAsView(List<Notification> notifications) {
      List<Notification> readNotifications = notifications.stream().peek(notification -> notification.setSeen(true)).toList();
      notificationRepository.saveAll(readNotifications);
    }

    public void save(NotificationRequestDto notificationRequestDto) {
    try {
      // Id to User map
      Map<String, User> userMap =
          userService.getAllByIds(notificationRequestDto.getReceiverIds()).stream()
              .collect(java.util.stream.Collectors.toMap(User::getId, user -> user));

      User loggedInUser =
          userService.getById(TokenUtils.getCurrentUserId()).orElseThrow();
      List<Notification> notifications =
          notificationRequestDto.getReceiverIds().stream()
              .map(
                  receiverId -> {
                    Notification notification = new Notification();
                    BeanUtils.copyProperties(notificationRequestDto, notification, "receiverIds");
                    notification.setCreatedBy(TokenUtils.getCurrentUserId());
                    notification.setReceiver(userMap.get(receiverId));
                    notification.setSender(loggedInUser);
                    notificationRepository.save(notification);
                    return notification;
                  })
              .toList();
      notificationRepository.saveAll(notifications);
    } catch (Exception e) {
      log.error("Error saving notifications: {}", e.getMessage());
    }
  }

  public Integer getLoggedUserUnseenNotificationsCount() {
    return notificationRepository.countByReceiverIdAndSeen(TokenUtils.getCurrentUserId(), false);
  }
}
