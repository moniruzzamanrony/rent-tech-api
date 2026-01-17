package com.itvillage.renttech.dynamicform;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum QuestionType {

    INPUT(100),
    DROPDOWN(105),
    CHECKBOX(110),
    RADIO(115),
    TEXTAREA(120),
    MULTISELECT(125);

    private final int code;

    QuestionType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    private static final Map<Integer, QuestionType> CODE_MAP =
            Stream.of(values())
                    .collect(Collectors.toMap(QuestionType::getCode, q -> q));

    public static QuestionType fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        QuestionType type = CODE_MAP.get(code);
        if (type == null) {
            throw new IllegalArgumentException("Invalid QuestionType code: " + code);
        }
        return type;
    }

    @Converter(autoApply = false)
    public static class QuestionTypeConverter
            implements AttributeConverter<QuestionType, Integer> {

        @Override
        public Integer convertToDatabaseColumn(QuestionType type) {
            return type != null ? type.getCode() : null;
        }

        @Override
        public QuestionType convertToEntityAttribute(Integer dbCode) {
            return QuestionType.fromCode(dbCode);
        }
    }
}
