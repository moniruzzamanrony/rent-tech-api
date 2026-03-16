package com.itvillage.renttech.payment;

import com.itvillage.renttech.base.model.MagicBaseModel;
import com.itvillage.renttech.verification.user.UserPackage;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
public class Payment extends MagicBaseModel {

    @ManyToOne(fetch = FetchType.LAZY)
    private UserPackage userPackage;

    // Order ID / Merchant Order ID
    private String orderId;

    // Transaction ID from EPS
    private String transactionId;

    // Redirect URL returned by EPS
    private String paymentUrl;

    // Amount in BDT
    private double amount;

    // Payment status
    @Convert(converter = PaymentStatus.PaymentStatusConverter.class)
    private PaymentStatus status;

    // Raw JSON response from EPS
    @Lob
    private String gatewayResponse;


}
