package com.misyakuji.service;

import com.misyakuji.entity.UserInfo;

public interface UserInfoService {
    UserInfo getUserInfoByUserId(Long userId);
    void createUserInfo(UserInfo userInfo);
    void updateUserInfo(UserInfo userInfo);
}