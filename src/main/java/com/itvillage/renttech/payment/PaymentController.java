package com.itvillage.renttech.payment;

import com.itvillage.renttech.base.constants.ApiConstant;
import com.itvillage.renttech.base.dto.APIResponseDto;
import com.itvillage.renttech.payment.eps.EpsPaymentResponse;
import com.itvillage.renttech.payment.eps.EpsPaymentService;
import com.itvillage.renttech.payment.eps.EpsTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final EpsPaymentService epsPaymentService;

    @GetMapping(ApiConstant.PRIVATE_BASE_API+"/payments/token")
    public APIResponseDto<EpsTokenResponse> getToken() {
        return new APIResponseDto<>(HttpStatus.OK.value(), epsPaymentService.getToken());
    }

    /**
     * Create payment session
     */
    @PostMapping(ApiConstant.PRIVATE_BASE_API+"/payments/create")
    public APIResponseDto<EpsPaymentResponse> createPayment(@RequestBody PaymentRequest request) {
        return new APIResponseDto<>(HttpStatus.OK.value(), epsPaymentService.createPayment(request));
    }

    @GetMapping(ApiConstant.PRIVATE_BASE_API+"/payments/success")
    public VerificationDto paymentSuccess(
            @RequestParam("Status") String status,
            @RequestParam("MerchantTransactionId") String merchantTransactionId,
            @RequestParam(value = "EPSTransactionId ", required = false) String epsTransactionId,
            @RequestParam(value = "ErrorCode", required = false) String errorCode
    ) {

       return epsPaymentService.updatePaymentStatus(
                PaymentStatus.SUCCESS,
                merchantTransactionId,
                epsTransactionId,
                status,
                errorCode
        );
    }

    @GetMapping(ApiConstant.PRIVATE_BASE_API+"/payments/fail")
    public VerificationDto paymentFailed(
            @RequestParam("Status") String status,
            @RequestParam("MerchantTransactionId") String merchantTransactionId,
            @RequestParam(value = "EPSTransactionId", required = false) String epsTransactionId,
            @RequestParam(value = "ErrorCode", required = false) String errorCode
    ) {

        return  epsPaymentService.updatePaymentStatus(
                PaymentStatus.FAILED,
                merchantTransactionId,
                epsTransactionId,
                status,
                errorCode
        );

    }

    @GetMapping(ApiConstant.PRIVATE_BASE_API+"/payments/cancel")
    public VerificationDto paymentCancelled(
            @RequestParam("Status") String status,
            @RequestParam("MerchantTransactionId") String merchantTransactionId,
            @RequestParam(value = "EPSTransactionId", required = false) String epsTransactionId,
            @RequestParam(value = "ErrorCode", required = false) String errorCode
    ) {

        return  epsPaymentService.updatePaymentStatus(
                PaymentStatus.CANCELLED,
                merchantTransactionId,
                epsTransactionId,
                status,
                errorCode
        );

    }

    /**
     * Check payment status
     */
    @GetMapping(ApiConstant.PRIVATE_BASE_API+"/payments/status/{orderId}")
    public APIResponseDto<PaymentResponse> getPaymentStatus(@PathVariable String orderId) {
        return new APIResponseDto<>(HttpStatus.OK.value(), epsPaymentService.getPaymentStatus(orderId));
    }
}
