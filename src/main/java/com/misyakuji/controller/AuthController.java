package com.misyakuji.controller;

import com.misyakuji.entity.BizUser;
import com.misyakuji.repository.BizUserRepository;
import com.misyakuji.utils.JwtUtils;
import com.misyakuji.utils.TokenBlacklist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
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

        String role = auth.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_USER");
        String token = jwtUtils.generateToken(auth.getName(), role);

        return Map.of("token", token);
    }

    @PostMapping("/register")
    public Map<String, String> register(@RequestBody Map<String, String> registerRequest) {
        String username = registerRequest.get("username");
        String password = registerRequest.get("password");

        // 检查必填字段
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return Map.of("message", "请求参数不存在");
        }

        // 密码复杂度校验
        if (password.length() < 6) {
            return Map.of("message", "密码长度不能少于6位");
        }

        // 检查用户名是否已存在（返回统一消息防止用户枚举）
        if (bizUserRepository.findByUsername(username).isPresent()) {
            return Map.of("message", "注册失败，请检查输入");
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