package com.misyakuji.enums;

import lombok.Getter;

/**
 * 交易类型枚举类
 * 定义了系统支持的三种交易类型：借款、还款和利息
 */
@Getter
public enum TransactionType {
    /**
     * 借款类型
     * 表示借款人从债权人处获得资金的交易
     */
    LOAN("Loan", "借款"),
    
    /**
     * 还款类型
     * 表示借款人向债权人偿还借款的交易
     * 通常以负数金额表示
     */
    REPAYMENT("Repayment", "还款"),
    
    /**
     * 利息类型
     * 表示借款产生的利息费用
     */
    INTEREST("Interest", "利息");

    // 交易类型的英文标识
    private final String value;
    // 交易类型的中文描述
    private final String text;

    /**
     * 构造函数
     * @param value 交易类型的英文标识
     * @param text 交易类型的中文描述
     */
    TransactionType(String value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 根据英文标识获取对应的交易类型枚举实例
     * @param value 交易类型的英文标识
     * @return 对应的TransactionType枚举实例，如果没有匹配的则返回null
     */
    public static TransactionType getByValue(String value) {
        for (TransactionType type : values()) {
            if (value.equals(type.getValue())) {
                return type;
            }
        }
        return null;
    }

    public static TransactionType getByText(String text) {
        for (TransactionType type : values()) {
            if (text.equals(type.getText())) {
                return type;
            }
        }
        return null;
    }
}
