package com.misyakuji.entity;

import lombok.Data;

@Data
public class UserInfo {
    private Long id;
    private Long userId;
    private String mobile;
    private String phone;
    private String email;
    private String seat;
    private String entity;
    private String leader;
    private String position;
    private String joinDay;
    private String group;
}