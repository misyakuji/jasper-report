package com.misyakuji.controller;

import com.misyakuji.entity.User;
import com.misyakuji.entity.UserInfo;
import com.misyakuji.mapper.UserMapper;
import com.misyakuji.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserInfoController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserInfoService userInfoService;

    @GetMapping("/info")
    public Map<String, Object> getUserInfo(Authentication authentication) {
        // 从Authentication中获取用户名
        String username = authentication.getName();
        // 根据用户名查询用户信息
        User user = userMapper.findByUsername(username);
        if (user == null) {
            return null;
        }
        // 根据用户id查询用户详情
        UserInfo userInfo = userInfoService.getUserInfoByUserId(user.getId());
        if (userInfo == null) {
            // 如果没有用户详情，返回一个默认的用户详情
            userInfo = new UserInfo();
            userInfo.setUserId(user.getId());
            userInfo.setMobile("+86 13923734567");
            userInfo.setPhone("734567");
            userInfo.setEmail("Account@qq.com");
            userInfo.setSeat("T32F 012");
            userInfo.setEntity("腾讯集团");
            userInfo.setLeader("Michael Wang");
            userInfo.setPosition("高级 UI 设计师");
            userInfo.setJoinDay("2021-07-01");
            userInfo.setGroup("腾讯/腾讯公司/某事业群/某产品部/某运营中心/商户服务组");
            // 保存默认的用户详情
            userInfoService.createUserInfo(userInfo);
        }
        
        // 返回包含name字段的Map，其中name字段值为username
        Map<String, Object> result = new HashMap<>();
        result.put("name", username);
        result.put("id", userInfo.getId());
        result.put("userId", user.getId()); // 使用user.getId()而不是userInfo.getUserId()
        result.put("mobile", userInfo.getMobile());
        result.put("phone", userInfo.getPhone());
        result.put("email", userInfo.getEmail());
        result.put("seat", userInfo.getSeat());
        result.put("entity", userInfo.getEntity());
        result.put("leader", userInfo.getLeader());
        result.put("position", userInfo.getPosition());
        result.put("joinDay", userInfo.getJoinDay() != null ? userInfo.getJoinDay() : "2021-07-01"); // 确保joinDay字段有值
        result.put("group", userInfo.getGroup());
        
        return result;
    }
}