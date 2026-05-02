package com.itvillage.renttech.signupreward;


import com.itvillage.renttech.base.model.MagicBaseModel;
import jakarta.persistence.Entity;
import lombok.Data;
import org.hibernate.annotations.SQLRestriction;

@SQLRestriction("is_deleted = false")
@Entity
@Data
public class SignUpReward extends MagicBaseModel {
    private int numberOfCoins;
}
