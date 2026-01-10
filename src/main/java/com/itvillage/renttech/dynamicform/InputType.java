package com.itvillage.renttech.dynamicform;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum InputType {

    DATE(100),
    NUMERIC(105),
    TEXT(110);

    private final int code;

    InputType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    private static final Map<Integer, InputType> CODE_MAP =
            Stream.of(values())
                    .collect(Collectors.toMap(InputType::getCode, t -> t));

    public static InputType fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        InputType type = CODE_MAP.get(code);
        if (type == null) {
            throw new IllegalArgumentException("Invalid InputType code: " + code);
        }
        return type;
    }

    @Converter(autoApply = false)
    public static class InputTypeConverter
            implements AttributeConverter<InputType, Integer> {

        @Override
        public Integer convertToDatabaseColumn(InputType type) {
            return type != null ? type.getCode() : null;
        }

        @Override
        public InputType convertToEntityAttribute(Integer dbCode) {
            return InputType.fromCode(dbCode);
        }
    }
}
