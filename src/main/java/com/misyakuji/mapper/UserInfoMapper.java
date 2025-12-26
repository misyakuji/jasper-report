package com.misyakuji.mapper;

import com.misyakuji.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserInfoMapper {
    UserInfo findByUserId(@Param("userId") Long userId);
    void insertUserInfo(UserInfo userInfo);
    void updateUserInfo(UserInfo userInfo);
    void updateUserInfoSelective(UserInfo userInfo);
    void deleteUserInfoByUserId(@Param("userId") Long userId);
}