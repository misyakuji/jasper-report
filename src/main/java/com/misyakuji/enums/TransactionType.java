package com.misyakuji.enums;

import lombok.Getter;

@Getter
public enum TransactionType {
    LOAN("Loan", "借款"),
    REPAYMENT("Repayment", "还款"),
    INTEREST("Interest", "利息");

    private final String value;
    private final String text;

    TransactionType(String value, String text) {
        this.value = value;
        this.text = text;
    }

    public static TransactionType getByValue(String value) {
        for (TransactionType type : values()) {
            if (value.equals(type.getValue())) {
                return type;
            }
        }
        return null;
    }
}
