package com.misyakuji.enums;

import lombok.Getter;

@Getter
public enum RoleType {
    GUEST(0, "GUEST", "访客"),
    USER(1, "USER", "普通用户"),
    ADMIN(2, "ADMIN", "管理员");

    private final Integer value;
    private final String role;
    private final String description;

    RoleType(Integer value, String role, String description) {
        this.value = value;
        this.role = role;
        this.description = description;
    }

    public static String getRole(Integer value) {
        for (RoleType type : values()) {
            if (value.equals(type.getValue())) {
                return type.role;
            }
        }
        return GUEST.role;
    }
    public static RoleType getRoleType(Integer value) {
        for (RoleType type : values()) {
            if (value.equals(type.getValue())) {
                return type;
            }
        }
        return GUEST;
    }
}
