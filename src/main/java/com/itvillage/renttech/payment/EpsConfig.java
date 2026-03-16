package com.itvillage.renttech.payment;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "payment-gateway.eps")
public class EpsConfig {
    private String hostUrl;
    private String merchantId;
    private String storeId;
    private String username;
    private String password;
    private String hashKey;
}
