package com.itvillage.renttech.verification.user;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Gender {

    MALE(100),
    FEMALE(101),
    OTHER(102);

    private final int code;

    Gender(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    private static final Map<Integer, Gender> CODE_MAP =
            Stream.of(values())
                    .collect(Collectors.toMap(Gender::getCode, g -> g));

    public static Gender fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        Gender gender = CODE_MAP.get(code);
        if (gender == null) {
            throw new IllegalArgumentException("Invalid Gender code: " + code);
        }
        return gender;
    }

    @Converter(autoApply = false)
    public static class GenderConverter
            implements AttributeConverter<Gender, Integer> {

        @Override
        public Integer convertToDatabaseColumn(Gender gender) {
            return gender != null ? gender.getCode() : null;
        }

        @Override
        public Gender convertToEntityAttribute(Integer dbCode) {
            return Gender.fromCode(dbCode);
        }
    }
}
