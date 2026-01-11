package com.itvillage.renttech.packages;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum PackageType {

    SEARCHING_PACKAGE(101),
    POST_ADS_PACKAGE(102);

    private final int code;

    PackageType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    // Fast lookup map (O(1))
    private static final Map<Integer, PackageType> CODE_MAP =
            Stream.of(values())
                    .collect(Collectors.toMap(PackageType::getCode, p -> p));

    public static PackageType fromCode(Integer code) {
        if (code == null) return null;

        PackageType type = CODE_MAP.get(code);
        if (type == null) {
            throw new IllegalArgumentException("Invalid PackageType code: " + code);
        }
        return type;
    }

    @Converter(autoApply = false)
    public static class PackageTypeConverter
            implements AttributeConverter<PackageType, Integer> {

        @Override
        public Integer convertToDatabaseColumn(PackageType type) {
            return type != null ? type.getCode() : null;
        }

        @Override
        public PackageType convertToEntityAttribute(Integer dbCode) {
            return PackageType.fromCode(dbCode);
        }
    }
}
