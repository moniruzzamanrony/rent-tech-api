package com.itvillage.renttech.dynamicform;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum PurposeType {

    SPECIFICATION(100),
    AMENITIES(105),
    OTHERS(110);

    private final int code;

    PurposeType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    private static final Map<Integer, PurposeType> CODE_MAP =
            Stream.of(values())
                    .collect(Collectors.toMap(PurposeType::getCode, type -> type));

    public static PurposeType fromCode(Integer code) {
        if (code == null) {
            return null;
        }

        PurposeType type = CODE_MAP.get(code);
        if (type == null) {
            throw new IllegalArgumentException("Invalid PurposeType code: " + code);
        }

        return type;
    }

    @Converter(autoApply = false)
    public static class PurposeTypeConverter implements AttributeConverter<PurposeType, Integer> {

        @Override
        public Integer convertToDatabaseColumn(PurposeType attribute) {
            return attribute != null ? attribute.getCode() : null;
        }

        @Override
        public PurposeType convertToEntityAttribute(Integer dbData) {
            return PurposeType.fromCode(dbData);
        }
    }
}
