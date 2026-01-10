package com.itvillage.renttech.verification.user;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

public enum ProfileStatus {
    VERIFIED(100),
    PENDING(101),
    INACTIVE(102);

    private final int code;

    ProfileStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static ProfileStatus fromCode(int code) {
        for (ProfileStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid ProfileStatus code: " + code);
    }

    @Converter(autoApply = false)
    public static class ProfileStatusConverter implements AttributeConverter<ProfileStatus, Integer> {

        @Override
        public Integer convertToDatabaseColumn(ProfileStatus status) {
            return status != null ? status.getCode() : null;
        }

        @Override
        public ProfileStatus convertToEntityAttribute(Integer dbCode) {
            return dbCode != null ? ProfileStatus.fromCode(dbCode) : null;
        }
    }
}

