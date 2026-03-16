package com.itvillage.renttech.payment;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PaymentResponse {

    // Your system's order ID
    private String orderId;

    // Payment amount
    private double amount;

    // Current payment status (INIT, SUCCESS, FAILED, etc.)
    private PaymentStatus status;

    // EPS transaction ID
    private String transactionId;
}
