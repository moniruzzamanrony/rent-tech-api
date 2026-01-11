package com.itvillage.renttech.signupreward;


import com.itvillage.renttech.base.model.MagicBaseModel;
import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
public class SignUpReward extends MagicBaseModel {
    private int numberOfCoins;
}
