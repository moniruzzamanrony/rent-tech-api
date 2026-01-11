package com.itvillage.renttech.notification;



import com.itvillage.renttech.base.model.MagicBaseModel;
import com.itvillage.renttech.verification.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "notification",
        indexes = {
                @Index(name = "idx_notification_receiver_seen", columnList = "receiver_id, seen"),
                @Index(name = "idx_notification_created_date", columnList = "created_date")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification extends MagicBaseModel {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(length = 1000)
    private String details;

    @Column(nullable = false)
    private boolean seen = false;
}

