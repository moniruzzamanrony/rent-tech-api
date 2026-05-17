package com.itvillage.renttech.signupreward;


import com.itvillage.renttech.base.model.MagicBaseModel;
import jakarta.persistence.Entity;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@SQLDelete(sql = "UPDATE sign_up_reward SET is_deleted = true WHERE id = ? AND version = ?")
@SQLRestriction("is_deleted = false")
@Entity
@Data
public class SignUpReward extends MagicBaseModel {
    private int numberOfCoins;
}
