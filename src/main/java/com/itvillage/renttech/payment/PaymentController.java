package com.itvillage.renttech.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final EpsPaymentService epsPaymentService;

    @GetMapping("/token")
    public EpsTokenResponse getToken() {
        return epsPaymentService.getToken();
    }

    /**
     * Create payment session
     */
    @PostMapping("/create")
    public EpsPaymentResponse createPayment(@RequestBody EpsCreatePaymentRequest request) {
        return epsPaymentService.createPayment(request);
    }

    /**
     * EPS payment success redirect/callback
     */
    @GetMapping("/success")
    public String paymentSuccess(
            @RequestParam String TransactionId,
            @RequestParam String Status,
            @RequestParam String OrderId
    ) {
        epsPaymentService.updatePaymentStatus(TransactionId, Status);
        return "Payment SUCCESS for order: " + OrderId;
    }

    /**
     * EPS payment failure redirect/callback
     */
    @GetMapping("/fail")
    public String paymentFailed(
            @RequestParam String TransactionId,
            @RequestParam String Status,
            @RequestParam String OrderId
    ) {
        epsPaymentService.updatePaymentStatus(TransactionId, Status);
        return "Payment FAILED for order: " + OrderId;
    }

    /**
     * EPS payment cancel redirect/callback
     */
    @GetMapping("/cancel")
    public String paymentCancelled(
            @RequestParam String TransactionId,
            @RequestParam String Status,
            @RequestParam String OrderId
    ) {
        epsPaymentService.updatePaymentStatus(TransactionId, Status);
        return "Payment CANCELLED for order: " + OrderId;
    }

    /**
     * Check payment status
     */
    @GetMapping("/status/{orderId}")
    public PaymentResponse getPaymentStatus(@PathVariable String orderId) {
        return epsPaymentService.getPaymentStatus(orderId);
    }
}
