package com.itvillage.renttech.creditrecharge;

import com.itvillage.renttech.base.model.MagicBaseModel;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.Data;
import org.hibernate.annotations.SQLRestriction;

@SQLRestriction("is_deleted = false")
@Entity
@Data
@Table(name = "credit_recharge")
public class CreditRecharge extends MagicBaseModel {

    @Column(name = "cost_per_credit")
    private int costPerCredit;

    @Column(name = "min_credit_purchase_limit")
    private int minCreditPurchaseLimit;
}
