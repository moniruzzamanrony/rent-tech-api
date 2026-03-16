package com.itvillage.renttech.payment;


import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EpsPaymentService {

    private final EpsConfig epsConfig;
    private final PaymentRepository paymentRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Create EPS payment session
     */
    public EpsPaymentResponse createPayment(EpsCreatePaymentRequest request) {

        // 1️⃣ Save INIT payment in DB
        Payment payment = new Payment();
        payment.setOrderId(request.getCustomerOrderId());
        payment.setAmount(request.getTotalAmount());
        payment.setStatus(PaymentStatus.INIT);
        paymentRepository.save(payment);

        // 2️⃣ Call EPS API
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EpsCreatePaymentRequest> entity = new HttpEntity<>(request, headers);

        String initUrl = epsConfig.getHostUrl()+"/EPSEngine/InitializeEPS";
        ResponseEntity<EpsPaymentResponse> responseEntity = restTemplate.exchange(
                initUrl,
                HttpMethod.POST,
                entity,
                EpsPaymentResponse.class
        );

        EpsPaymentResponse epsResponse = responseEntity.getBody();

        // 3️⃣ Update DB with TransactionId if returned
        if (epsResponse != null && epsResponse.getTransactionId() != null) {
            payment.setTransactionId(epsResponse.getTransactionId());
            paymentRepository.save(payment);
        }

        return epsResponse;
    }

    /**
     * Update payment status based on redirect query params
     */
    public void updatePaymentStatus(String transactionId, String status) {
        Optional<Payment> optionalPayment = paymentRepository.findByTransactionId(transactionId);
        if (optionalPayment.isPresent()) {
            Payment payment = optionalPayment.get();
            switch (status.toUpperCase()) {
                case "SUCCESS" -> payment.setStatus(PaymentStatus.SUCCESS);
                case "FAILED" -> payment.setStatus(PaymentStatus.FAILED);
                case "CANCELLED" -> payment.setStatus(PaymentStatus.CANCELLED);
                default -> payment.setStatus(PaymentStatus.FAILED);
            }
            paymentRepository.save(payment);
        }
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
