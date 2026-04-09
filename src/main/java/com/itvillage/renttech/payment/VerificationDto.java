package com.itvillage.renttech.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VerificationDto {
    @JsonProperty("isSuccess")
    private boolean isSuccess;

    private String message;
}
