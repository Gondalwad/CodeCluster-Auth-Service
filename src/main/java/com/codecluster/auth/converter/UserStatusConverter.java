package com.codecluster.auth.converter;

import com.codecluster.auth.entity.UserStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class UserStatusConverter
        implements AttributeConverter<UserStatus, String> {

    @Override
    public String convertToDatabaseColumn(UserStatus status) {

        if (status == null) {
            return null;
        }

        return status.name().toLowerCase();
    }

    @Override
    public UserStatus convertToEntityAttribute(String dbValue) {

        if (dbValue == null) {
            return null;
        }

        return UserStatus.valueOf(dbValue.toUpperCase());
    }
}