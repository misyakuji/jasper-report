package com.misyakuji.converter;

import com.misyakuji.enums.TransactionType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TransactionTypeConverter implements AttributeConverter<TransactionType, String> {
    @Override
    public String convertToDatabaseColumn(TransactionType type) {
        return type != null ? type.getValue() : null;
    }

    @Override
    public TransactionType convertToEntityAttribute(String value) {
        return value != null ? TransactionType.getByValue(value) : null;
    }
}