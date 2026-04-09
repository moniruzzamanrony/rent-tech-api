package com.itvillage.renttech.payment;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VerificationDto {
    private boolean isSuccess;
        private String message;
}
