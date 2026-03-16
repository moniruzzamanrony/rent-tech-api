package com.itvillage.renttech.payment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillingAddressDto {
    private String customerName;
    private String customerEmail;
    private String customerAddress;
    private String customerCity;
    private String customerState;
    private String customerPostcode;
    private String customerCountry;
    private String customerPhone;
}
