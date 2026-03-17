package com.itvillage.renttech.payment;

import com.itvillage.renttech.base.constants.ApiConstant;
import com.itvillage.renttech.payment.eps.EpsPaymentResponse;
import com.itvillage.renttech.payment.eps.EpsPaymentService;
import com.itvillage.renttech.payment.eps.EpsTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final EpsPaymentService epsPaymentService;

    @GetMapping(ApiConstant.PRIVATE_BASE_API+"/payments/token")
    public EpsTokenResponse getToken() {
        return epsPaymentService.getToken();
    }

    /**
     * Create payment session
     */
    @PostMapping(ApiConstant.PRIVATE_BASE_API+"/payments/create")
    public EpsPaymentResponse createPayment(@RequestBody PaymentRequest request) {
        return epsPaymentService.createPayment(request);
    }

    @GetMapping(ApiConstant.PUBLIC_BASE_API+"/payments/success")
    public String paymentSuccess(
            @RequestParam("Status") String status,
            @RequestParam("MerchantTransactionId") String merchantTransactionId,
            @RequestParam(value = "EPSTransactionId ", required = false) String epsTransactionId,
            @RequestParam(value = "ErrorCode", required = false) String errorCode
    ) {

        epsPaymentService.updatePaymentStatus(
                PaymentStatus.SUCCESS,
                merchantTransactionId,
                epsTransactionId,
                status,
                errorCode
        );

        return "Payment SUCCESS for order: " + merchantTransactionId;
    }

    @GetMapping(ApiConstant.PUBLIC_BASE_API+"/payments/fail")
    public String paymentFailed(
            @RequestParam("Status") String status,
            @RequestParam("MerchantTransactionId") String merchantTransactionId,
            @RequestParam(value = "EPSTransactionId", required = false) String epsTransactionId,
            @RequestParam(value = "ErrorCode", required = false) String errorCode
    ) {

        epsPaymentService.updatePaymentStatus(
                PaymentStatus.FAILED,
                merchantTransactionId,
                epsTransactionId,
                status,
                errorCode
        );

        return "Payment FAILED for order: " + merchantTransactionId;
    }

    @GetMapping(ApiConstant.PUBLIC_BASE_API+"/payments/cancel")
    public String paymentCancelled(
            @RequestParam("Status") String status,
            @RequestParam("MerchantTransactionId") String merchantTransactionId,
            @RequestParam(value = "EPSTransactionId", required = false) String epsTransactionId,
            @RequestParam(value = "ErrorCode", required = false) String errorCode
    ) {

        epsPaymentService.updatePaymentStatus(
                PaymentStatus.CANCELLED,
                merchantTransactionId,
                epsTransactionId,
                status,
                errorCode
        );

        return "Payment CANCELLED for order: " + merchantTransactionId;
    }

    /**
     * Check payment status
     */
    @GetMapping(ApiConstant.PRIVATE_BASE_API+"/payments/status/{orderId}")
    public PaymentResponse getPaymentStatus(@PathVariable String orderId) {
        return epsPaymentService.getPaymentStatus(orderId);
    }
}
