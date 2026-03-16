package com.itvillage.renttech.payment;

import com.itvillage.renttech.payment.eps.EpsPaymentResponse;
import com.itvillage.renttech.payment.eps.EpsPaymentService;
import com.itvillage.renttech.payment.eps.EpsTokenResponse;
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
    public EpsPaymentResponse createPayment(@RequestBody PaymentRequest request) {
        return epsPaymentService.createPayment(request);
    }

    /**
     * EPS payment success redirect/callback
     */
    @GetMapping("/success")
    public String paymentSuccess(
            @RequestParam String data
    ) {
        epsPaymentService.updatePaymentStatus(data);
        return "Payment SUCCESS for order";
    }

    /**
     * EPS payment failure redirect/callback
     */
    @GetMapping("/fail")
    public String paymentFailed(
            @RequestParam String data
    ) {
        epsPaymentService.updatePaymentStatus(data);
        return "Payment FAILED for order " ;
    }

    /**
     * EPS payment cancel redirect/callback
     */
    @GetMapping("/cancel")
    public String paymentCancelled(
            @RequestParam String data
    ) {
        epsPaymentService.updatePaymentStatus(data);
        return "Payment CANCELLED for order";
    }

    /**
     * Check payment status
     */
    @GetMapping("/status/{orderId}")
    public PaymentResponse getPaymentStatus(@PathVariable String orderId) {
        return epsPaymentService.getPaymentStatus(orderId);
    }
}
