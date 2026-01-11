package com.itvillage.renttech.creditrecharge;



import com.itvillage.renttech.base.model.MagicBaseModel;
import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
public class CreditRecharge extends MagicBaseModel {
    private int costPerCredit;
    private int minCreditPurchaseLimit;
}
