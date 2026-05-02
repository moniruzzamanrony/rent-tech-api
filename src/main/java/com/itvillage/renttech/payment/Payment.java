package com.itvillage.renttech.payment;

import com.itvillage.renttech.base.model.MagicBaseModel;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLRestriction;

@SQLRestriction("is_deleted = false")
@Entity
@Data
@Table(name = "payment")
public class Payment extends MagicBaseModel {

    @Column(name = "user_id")
    private String userId;

    @Column(name = "order_id")
    private String orderId;

    // Transaction ID from EPS
    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "merchant_transaction_id")
    private String merchantTransactionId;

    // Redirect URL returned by EPS
    @Column(name = "payment_url")
    private String paymentUrl;

    @Column(name = "error_code")
    private String errorCode;

    // Amount in BDT
    @Column(name = "amount")
    private double amount;

    @Column(name = "coin_qty")
    private int coinQty;

    // Payment status
    @Convert(converter = PaymentStatus.PaymentStatusConverter.class)
    @Column(name = "status")
    private PaymentStatus status;

    // Raw JSON response from EPS
    @Lob
    @Column(name = "gateway_response", columnDefinition = "TEXT")
    private String gatewayResponse;
}
