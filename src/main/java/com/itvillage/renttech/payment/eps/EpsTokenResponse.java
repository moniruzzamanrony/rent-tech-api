package com.itvillage.renttech.payment.eps;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EpsTokenResponse {

    // JWT token string
    private String token;

    // Token expiry datetime
    private LocalDateTime expireDate;

    // Any error message returned
    private String errorMessage;

    // Any error code returned
    private String errorCode;
}
