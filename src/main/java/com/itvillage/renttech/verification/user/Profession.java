package com.itvillage.renttech.verification.user;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Profession {

    PRIVATE_SERVICE_HOLDER(100),
    GOVT_SERVICE_HOLDER(101),
    BUSINESS(103),
    STUDENT(104),
    NONE(105);

    private final int code;

    Profession(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    // Map for code -> Profession lookup
    private static final Map<Integer, Profession> CODE_MAP =
            Stream.of(values())
                    .collect(Collectors.toMap(Profession::getCode, p -> p));

    // Static method to get Profession from code
    public static Profession fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        Profession profession = CODE_MAP.get(code);
        if (profession == null) {
            throw new IllegalArgumentException("Invalid Profession code: " + code);
        }
        return profession;
    }

    // JPA Attribute Converter
    @Converter(autoApply = false)
    public static class ProfessionConverter
            implements AttributeConverter<Profession, Integer> {

        @Override
        public Integer convertToDatabaseColumn(Profession profession) {
            return profession != null ? profession.getCode() : null;
        }

        @Override
        public Profession convertToEntityAttribute(Integer dbCode) {
            return Profession.fromCode(dbCode);
        }
    }
}
