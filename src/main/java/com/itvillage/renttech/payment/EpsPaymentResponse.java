package com.itvillage.renttech.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EpsPaymentResponse {
    @JsonProperty("TransactionId")
    private String transactionId;

    @JsonProperty("RedirectURL")
    private String redirectUrl;

    @JsonProperty("ErrorMessage")
    private String errorMessage;

    @JsonProperty("ErrorCode")
    private String errorCode;
}
