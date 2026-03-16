package com.itvillage.renttech.payment;

import lombok.experimental.UtilityClass;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@UtilityClass
public class EpsHashUtil {

    /**
     * Create Base64 HMAC-SHA512 hash
     *
     * @param hashKey The secret key provided by EPS (from YAML)
     * @param data    The data to hash (e.g., userId, orderId, etc.)
     * @return Base64 encoded HMAC-SHA512 hash
     */
    public String createHash(String hashKey, String data) {
        try {
            // Step 1: Encode Hash Key using UTF-8
            byte[] keyBytes = hashKey.getBytes(StandardCharsets.UTF_8);

            // Step 2: Create HMAC-SHA512
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "HmacSHA512");
            mac.init(secretKeySpec);

            // Step 3: Compute HMAC on the data
            byte[] hmacBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

            // Step 4: Return Base64 string of hash
            return Base64.getEncoder().encodeToString(hmacBytes);

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate EPS hash", e);
        }
    }
}
