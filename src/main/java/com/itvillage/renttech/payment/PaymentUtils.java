package com.itvillage.renttech.payment;

import lombok.experimental.UtilityClass;

import java.util.UUID;


@UtilityClass
public class PaymentUtils {
    public String generateOrderId() {
        return "RM-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }

    public String generateMerchantTransactionId() {
        return "TRX-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}
