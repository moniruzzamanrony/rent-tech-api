package com.itvillage.renttech.payment;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class PaymentRequest {

    @NotNull
    private Double totalAmount;

    @NotNull
    private BillingAddressDto billingAddressDto;

    @NotNull
    private int transactionTypeId;

    @NotNull
    private int coinQty;

}
