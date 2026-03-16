package com.itvillage.renttech.payment;

import lombok.Data;

import java.util.List;

@Data
public class EpsCreatePaymentRequest {
    private String storeId;
    private String merchantTransactionId;
    private String customerOrderId;

    private Integer transactionTypeId;
    private Integer financialEntityId;
    private Integer transitionStatusId;

    private double totalAmount;

    private String ipAddress;
    private String version;

    private String successUrl;
    private String failUrl;
    private String cancelUrl;

    // Customer info
    private String customerName;
    private String customerEmail;
    private String customerAddress;
    private String customerAddress2;
    private String customerCity;
    private String customerState;
    private String customerPostcode;
    private String customerCountry;
    private String customerPhone;

    // Shipment info
    private String shipmentName;
    private String shipmentAddress;
    private String shipmentAddress2;
    private String shipmentCity;
    private String shipmentState;
    private String shipmentPostcode;
    private String shipmentCountry;

    // Custom values
    private String valueA;
    private String valueB;
    private String valueC;
    private String valueD;

    // Product info
    private String shippingMethod;
    private String noOfItem;
    private String productName;
    private String productProfile;
    private String productCategory;

    private List<EpsProductItem> productList;
}
