package com.itvillage.renttech.payment;

import com.itvillage.renttech.verification.user.Profession;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum PaymentStatus {

    INIT(100),
    SUCCESS(101),
    FAILED(103),
    CANCELLED(104);

    private final int code;

    PaymentStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    // Map for code -> PaymentStatus lookup
    private static final Map<Integer, PaymentStatus> CODE_MAP =
            Stream.of(values())
                    .collect(Collectors.toMap(PaymentStatus::getCode, s -> s));

    public static PaymentStatus fromCode(Integer code) {
        if (code == null) {
            return null;
        }

        PaymentStatus status = CODE_MAP.get(code);

        if (status == null) {
            throw new IllegalArgumentException("Invalid PaymentStatus code: " + code);
        }

        return status;
    }

    @Converter(autoApply = false)
    public static class PaymentStatusConverter
            implements AttributeConverter<PaymentStatus, Integer> {

        @Override
        public Integer convertToDatabaseColumn(PaymentStatus status) {
            return status != null ? status.getCode() : null;
        }

        @Override
        public PaymentStatus convertToEntityAttribute(Integer dbCode) {
            return PaymentStatus.fromCode(dbCode);
        }
    }
}
