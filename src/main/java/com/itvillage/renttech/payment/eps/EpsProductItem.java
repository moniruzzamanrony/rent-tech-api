package com.itvillage.renttech.payment.eps;

import lombok.Data;

@Data
public class EpsProductItem {
    private String productName;
    private String noOfItem;
    private String productProfile;
    private String productCategory;
    private double productPrice;
}
