package com.itvillage.renttech.notification;



import com.itvillage.renttech.base.model.MagicBaseModel;
import com.itvillage.renttech.verification.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Notification extends MagicBaseModel {
    @ManyToOne(fetch = FetchType.LAZY)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    private User receiver;

    private String title;

    private String details;

    private boolean seen;

}
