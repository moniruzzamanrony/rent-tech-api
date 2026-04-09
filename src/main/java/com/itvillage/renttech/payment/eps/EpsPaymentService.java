package com.itvillage.renttech.payment.eps;


import com.itvillage.renttech.base.constants.ApiConstant;
import com.itvillage.renttech.base.expection.MagicException;
import com.itvillage.renttech.base.utils.TokenUtils;
import com.itvillage.renttech.payment.*;
import com.itvillage.renttech.rentpackages.RentPackageService;
import com.itvillage.renttech.verification.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EpsPaymentService {

    private final EpsConfig epsConfig;
    private final RentPackageService rentPackageService;
    private final PaymentRepository paymentRepository;
    private final UserService userService;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Create EPS payment session
     */
    public EpsPaymentResponse createPayment(PaymentRequest request) {
        String merchantTransactionId = PaymentUtils.generateMerchantTransactionId();

        // 1️⃣ Prepare EPS request
        EpsCreatePaymentRequest epsCreatePaymentRequest = new EpsCreatePaymentRequest();
        epsCreatePaymentRequest.setMerchantId(epsConfig.getMerchantId());
        epsCreatePaymentRequest.setStoreId(epsConfig.getStoreId());
        epsCreatePaymentRequest.setCustomerOrderId(PaymentUtils.generateOrderId());
        epsCreatePaymentRequest.setMerchantTransactionId(merchantTransactionId);
        epsCreatePaymentRequest.setTransactionTypeId(request.getTransactionTypeId());
        epsCreatePaymentRequest.setTotalAmount(request.getTotalAmount());

        epsCreatePaymentRequest.setCustomerName(request.getBillingAddressDto().getCustomerName());
        epsCreatePaymentRequest.setCustomerEmail(request.getBillingAddressDto().getCustomerEmail());
        epsCreatePaymentRequest.setCustomerPhone(request.getBillingAddressDto().getCustomerPhone());
        epsCreatePaymentRequest.setCustomerAddress(request.getBillingAddressDto().getCustomerAddress());
        epsCreatePaymentRequest.setCustomerCity(request.getBillingAddressDto().getCustomerCity());
        epsCreatePaymentRequest.setCustomerState(request.getBillingAddressDto().getCustomerState());
        epsCreatePaymentRequest.setCustomerPostcode(request.getBillingAddressDto().getCustomerPostcode());
        epsCreatePaymentRequest.setCustomerCountry(request.getBillingAddressDto().getCustomerCountry());

        epsCreatePaymentRequest.setProductName(request.getCoinQty() + " coins");
        epsCreatePaymentRequest.setCancelUrl(epsConfig.getMyHostUrl() + ApiConstant.PRIVATE_BASE_API+"/payments/cancel");
        epsCreatePaymentRequest.setFailUrl(epsConfig.getMyHostUrl() + ApiConstant.PRIVATE_BASE_API+"/payments/fail");
        epsCreatePaymentRequest.setSuccessUrl(epsConfig.getMyHostUrl() + ApiConstant.PRIVATE_BASE_API+"/payments/success");
        epsCreatePaymentRequest.setValueA(String.valueOf(request.getCoinQty()));

        // 2️⃣ Save INIT payment in DB
        Payment payment = new Payment();
        payment.setUserId(TokenUtils.getCurrentUserId());
        payment.setOrderId(epsCreatePaymentRequest.getCustomerOrderId());
        payment.setMerchantTransactionId(merchantTransactionId);
        payment.setAmount(epsCreatePaymentRequest.getTotalAmount());
        payment.setStatus(PaymentStatus.INIT);
        paymentRepository.save(payment);

        try {
            // 3️⃣ Generate hash & get token
            String hash = EpsHashUtil.createHash(epsConfig.getHashKey(), merchantTransactionId);
            EpsTokenResponse epsTokenResponse = getToken();
            if (epsTokenResponse.getToken().isBlank()) {
                throw new MagicException.BadRequestException("Invalid token: " + epsTokenResponse.getErrorMessage());
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.add("x-hash", hash);
            headers.add("Authorization", "Bearer " + epsTokenResponse.getToken());
            HttpEntity<EpsCreatePaymentRequest> entity = new HttpEntity<>(epsCreatePaymentRequest, headers);

            String initUrl = epsConfig.getHostUrl() + "/EPSEngine/InitializeEPS";
            ResponseEntity<EpsPaymentResponse> responseEntity = restTemplate.exchange(
                    initUrl,
                    HttpMethod.POST,
                    entity,
                    EpsPaymentResponse.class
            );
            HttpStatusCode statusCode = responseEntity.getStatusCode();
            HttpHeaders responseHeaders = responseEntity.getHeaders();
            EpsPaymentResponse epsResponse = responseEntity.getBody();
            // ✅ Save full response for debugging (VERY IMPORTANT)
            payment.setGatewayResponse(
                    "STATUS: " + statusCode +
                            " | HEADERS: " + responseHeaders.toString() +
                            " | BODY: " + (epsResponse != null ? epsResponse.toString() : "NULL")
            );

            if (epsResponse == null) {
                throw new RuntimeException("EPS API returned null response");
            }

            // 4️⃣ Update DB with transaction info if available
            if (epsResponse.getTransactionId() != null) {
                payment.setPaymentUrl(epsResponse.getRedirectUrl());
                payment.setCoinQty(request.getCoinQty());
            }
            paymentRepository.save(payment);
            return epsResponse;

        } catch (HttpClientErrorException e) {
            System.err.println("EPS Client error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            throw new RuntimeException("EPS payment failed due to client error", e);

        } catch (HttpServerErrorException e) {
            System.err.println("EPS Server error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            throw new RuntimeException("EPS payment failed due to server error", e);

        } catch (ResourceAccessException e) {
            System.err.println("EPS network error: " + e.getMessage());
            throw new RuntimeException("EPS payment failed due to network issue", e);

        } catch (Exception e) {
            System.err.println("Unexpected EPS error: " + e.getMessage());
            throw new RuntimeException("EPS payment failed unexpectedly", e);
        }
    }

    /**
     * Update payment status based on redirect query params
     */
    public VerificationDto updatePaymentStatus(
            PaymentStatus status,
            String merchantTransactionId,
            String epsTransactionId,
            String epsStatus,
            String errorCode
    ) {
        VerificationDto verificationDto = new VerificationDto();
        verificationDto.setMessage("Redirecting...");
        System.out.println("PaymentStatus Enum: " + status);
        System.out.println("EPS Status: " + epsStatus);
        System.out.println("MerchantTxnId: " + merchantTransactionId);
        System.out.println("EPSTxnId: " + epsTransactionId);
        System.out.println("ErrorCode: " + errorCode);

        Optional<Payment> optionalPayment =
                paymentRepository.findByMerchantTransactionId(merchantTransactionId);

        if (optionalPayment.isEmpty()) {
            System.err.println("Payment not found for MerchantTransactionId: " + merchantTransactionId);
            verificationDto.setSuccess(false);
        }

        Payment payment = optionalPayment.get();

        // ✅ Prevent duplicate update (VERY IMPORTANT)
        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            System.out.println("Payment already SUCCESS. Skipping update.");
            verificationDto.setSuccess(false);
        }

        // ✅ Update status based on EPS response (more reliable than URL path)
        if ("Success".equalsIgnoreCase(epsStatus)) {
            payment.setStatus(PaymentStatus.SUCCESS);

            // Save EPS transaction ID
            if (epsTransactionId != null && !epsTransactionId.isBlank()) {
                payment.setTransactionId(epsTransactionId.trim());

                try {
                    userService.addCoins(payment.getUserId(),payment.getCoinQty());
                    verificationDto.setSuccess(true);
                } catch (Exception e) {
                    System.err.println("Failed to add coins to user: " + e.getMessage());
                }
            }

        } else if ("Failed".equalsIgnoreCase(epsStatus)) {
            payment.setStatus(PaymentStatus.FAILED);
            verificationDto.setSuccess(false);

        } else if ("Cancelled".equalsIgnoreCase(epsStatus)) {
            payment.setStatus(PaymentStatus.CANCELLED);
            verificationDto.setSuccess(false);

        } else {
            payment.setStatus(PaymentStatus.FAILED);
            verificationDto.setSuccess(false);
        }

        payment.setErrorCode(errorCode);

        paymentRepository.save(payment);

        System.out.println("Payment updated successfully: " + merchantTransactionId);
        return  verificationDto;
    }

    /**
     * Get payment status by orderId
     */
    public PaymentResponse getPaymentStatus(String orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderId));

        return PaymentResponse.builder()
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .transactionId(payment.getTransactionId())
                .build();
    }

    public EpsTokenResponse getToken() {

        // 1️⃣ Prepare request DTO
        EpsTokenRequest request = new EpsTokenRequest();
        request.setUserName(epsConfig.getUsername());
        request.setPassword(epsConfig.getPassword());

        // 2️⃣ Prepare headers
        String hash = EpsHashUtil.createHash(epsConfig.getHashKey(), epsConfig.getUsername());
        HttpHeaders headers = new HttpHeaders();
        headers.add("x-hash", hash);

        HttpEntity<EpsTokenRequest> entity = new HttpEntity<>(request, headers);

        // 3️⃣ EPS token endpoint
        String initUrl = epsConfig.getHostUrl() + "/Auth/GetToken";

        // 4️⃣ Call EPS API
        ResponseEntity<EpsTokenResponse> responseEntity = restTemplate.exchange(
                initUrl,
                HttpMethod.POST,
                entity,
                EpsTokenResponse.class
        );

        // 5️⃣ Return the response
        return responseEntity.getBody();
    }
}
