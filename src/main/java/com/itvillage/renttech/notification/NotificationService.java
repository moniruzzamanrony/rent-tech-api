package com.itvillage.renttech.notification;


import com.itvillage.renttech.base.utils.ConverterUtils;
import com.itvillage.renttech.base.utils.TokenUtils;
import com.itvillage.renttech.verification.user.User;
import com.itvillage.renttech.verification.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
  private final NotificationRepository notificationRepository;
  private final UserService userService;

  @Transactional
  public Page<NotificationResponseDto> getLoggedUserNotifications(Pageable pageable) {
    String userId = TokenUtils.getCurrentUserId();
    Page<NotificationResponseDto> page = notificationRepository
        .findAllByReceiverIdOrderByCreatedDateDesc(userId, pageable)
        .map(ConverterUtils::convert);
    notificationRepository.markAllSeenByReceiverId(userId);
    return page;
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
