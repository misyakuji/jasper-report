package com.misyakuji.controller;

import com.misyakuji.entity.BizUser;
import com.misyakuji.repository.BizUserRepository;
import com.misyakuji.utils.JwtUtils;
import com.misyakuji.utils.TokenBlacklist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private TokenBlacklist tokenBlacklist;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private BizUserRepository bizUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        String token = jwtUtils.generateToken(user.getUsername(), user.getAuthorities().iterator().next().getAuthority());

        return Map.of("token", token);
    }

    @PostMapping("/register")
    public Map<String, String> register(@RequestBody Map<String, String> registerRequest) {
        String username = registerRequest.get("username");
        String password = registerRequest.get("password");
        
        // 检查用户名是否已存在
        if (bizUserRepository.findByUsername(username).isPresent()) {
            return Map.of("message", "用户名已存在");
        }

        // 创建新用户
        BizUser bizUser = new BizUser();
        bizUser.setUsername(username);
        bizUser.setPasswordHash(passwordEncoder.encode(password));
        bizUser.setPermissionLevel(1);
        bizUser.setStatus(1);
        bizUserRepository.save(bizUser);

        return Map.of("message", "注册成功");
    }

    @PostMapping("/logout")
    public Map<String, String> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            tokenBlacklist.blacklistToken(token);
        }
        return Map.of("message", "Logged out successfully");
    }
}