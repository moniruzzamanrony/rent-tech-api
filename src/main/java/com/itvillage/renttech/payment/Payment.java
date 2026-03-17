package com.itvillage.renttech.payment;

import com.itvillage.renttech.base.model.MagicBaseModel;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import lombok.Data;

@Entity
@Data
public class Payment extends MagicBaseModel {

    // Order ID / Merchant Order ID
    private String orderId;

    // Transaction ID from EPS
    private String transactionId;

    private String merchantTransactionId;

    // Redirect URL returned by EPS
    private String paymentUrl;

    private String errorCode;

    // Amount in BDT
    private double amount;

    private int coinQty;

    // Payment status
    @Convert(converter = PaymentStatus.PaymentStatusConverter.class)
    private PaymentStatus status;

    // Raw JSON response from EPS
    @Lob
    private String gatewayResponse;

}
