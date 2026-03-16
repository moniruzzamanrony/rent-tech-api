package com.itvillage.renttech.payment.eps;

import lombok.Data;

@Data
public class EpsTokenRequest {

    // EPS username
    private String userName;

    // EPS password
    private String password;
}
