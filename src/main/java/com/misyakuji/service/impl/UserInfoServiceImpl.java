package com.misyakuji.service.impl;

import com.misyakuji.entity.UserInfo;
import com.misyakuji.mapper.UserInfoMapper;
import com.misyakuji.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public UserInfo getUserInfoByUserId(Long userId) {
        return userInfoMapper.findByUserId(userId);
    }

    @Override
    public void createUserInfo(UserInfo userInfo) {
        userInfoMapper.insertUserInfo(userInfo);
    }

    @Override
    public void updateUserInfo(UserInfo userInfo) {
        userInfoMapper.updateUserInfo(userInfo);
    }
}