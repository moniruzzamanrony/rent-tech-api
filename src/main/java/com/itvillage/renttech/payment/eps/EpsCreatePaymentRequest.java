package com.itvillage.renttech.payment.eps;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;


@Data
public class EpsCreatePaymentRequest {

    // Mandatory fields
    @NotNull
    private String merchantId;

    @NotNull
    private String storeId;

    @NotNull
    private String customerOrderId;

    @NotNull
    private String merchantTransactionId;

    @NotNull
    private Integer transactionTypeId;

    @NotNull
    private Double totalAmount;

    @NotNull
    private String successUrl;

    @NotNull
    private String failUrl;

    @NotNull
    private String cancelUrl;

    @NotNull
    private String customerName;

    @NotNull
    private String customerEmail;

    @NotNull
    private String customerPhone;

    // Optional customer fields
    private String customerAddress;
    private String customerAddress2;
    private String customerCity;
    private String customerState;
    private String customerPostcode;
    private String customerCountry;

    // Optional shipment fields
    private String shipmentName;
    private String shipmentAddress;
    private String shipmentAddress2;
    private String shipmentCity;
    private String shipmentState;
    private String shipmentPostcode;
    private String shipmentCountry;

    // Optional custom values
    private String valueA;
    private String valueB;
    private String valueC;
    private String valueD;

    // Optional product/shipping info
    private String shippingMethod;
    private String noOfItem;
    private String productName;
    private String productProfile;
    private String productCategory;

    private List<EpsProductItem> productList;
}
