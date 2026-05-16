package com.itvillage.renttech.notification;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, String> {
    Page<Notification> findAllByReceiverIdOrderByCreatedDateDesc(String userId, Pageable pageable);
    int countByReceiverIdAndSeen(String userId, boolean seen);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Notification n SET n.seen = true WHERE n.receiver.id = :userId AND n.seen = false")
    int markAllSeenByReceiverId(@Param("userId") String userId);
}
