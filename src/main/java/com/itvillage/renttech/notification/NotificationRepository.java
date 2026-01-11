package com.itvillage.renttech.notification;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, String> {
    List<Notification> findAllByReceiverIdOrderByCreatedDateDesc(String userId);
    int countByReceiverIdAndSeen(String userId, boolean seen);
}
