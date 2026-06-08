package com.misyakuji.controller;

import com.misyakuji.entity.BizUser;
import com.misyakuji.entity.BizUserInfo;
import com.misyakuji.entity.Borrowers;
import com.misyakuji.service.BizUserInfoService;
import com.misyakuji.service.BizUserService;
import com.misyakuji.service.BorrowersService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户管理控制器
 * 提供用户账户信息和详细信息的增删改查及权限管理等RESTful API接口
 * 
 * @since v2.0 新增用户管理功能
 */
@RestController
@RequestMapping(value = "/users", produces = "application/json")
@CrossOrigin(origins = "*")
public class BizUserController {

    private final BizUserService bizUserService;
    private final BizUserInfoService bizUserInfoService;
    private final BorrowersService borrowersService;

    /**
     * 构造函数，通过依赖注入获取服务实例
     * @param bizUserService 用户服务层组件
     * @param bizUserInfoService 用户详细信息服务层组件
     * @param borrowersService 借款人服务层组件
     */
    public BizUserController(BizUserService bizUserService,
                             BizUserInfoService bizUserInfoService,
                             BorrowersService borrowersService) {
        this.bizUserService = bizUserService;
        this.bizUserInfoService = bizUserInfoService;
        this.borrowersService = borrowersService;
    }

    // ==================== 用户账户管理 ====================

    /**
     * 查询所有用户信息
     * @return 包含所有用户的列表及HTTP 200状态码
     */
    @GetMapping  // GET /users
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BizUser>> getAll() {
        return ResponseEntity.ok(bizUserService.getAll());
    }

    /**
     * 查询所有用户信息（包含详细信息）
     * @return 包含所有用户及其详细信息的列表及HTTP 200状态码
     */
    @GetMapping("/with-info")  // GET /users/with-info
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BizUser>> getAllWithUserInfo() {
        return ResponseEntity.ok(bizUserService.getAllWithUserInfo());
    }

    /**
     * 根据ID查询用户信息
     * @param userId 用户ID
     * @return 查询到的用户对象及HTTP 200状态码
     * @throws EntityNotFoundException 当指定ID的用户不存在时抛出
     */
    @GetMapping("/{userId}")  // GET /users/{userId}
    public ResponseEntity<BizUser> getById(@PathVariable Integer userId) {
        return ResponseEntity.ok(bizUserService.getById(userId));
    }

    /**
     * 根据ID查询用户信息（包含详细信息）
     * @param userId 用户ID
     * @return 查询到的用户对象（包含详细信息）及HTTP 200状态码
     * @throws EntityNotFoundException 当指定ID的用户不存在时抛出
     */
    @GetMapping("/{userId}/with-info")  // GET /users/{userId}/with-info
    public ResponseEntity<BizUser> getByIdWithUserInfo(@PathVariable Integer userId) {
        return ResponseEntity.ok(bizUserService.getByIdWithUserInfo(userId));
    }

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 查询到的用户对象及HTTP 200状态码
     */
    @GetMapping("/username/{username}")  // GET /users/username/{username}
    public ResponseEntity<BizUser> getByUsername(@PathVariable String username) {
        return bizUserService.getByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 创建新用户
     * @param bizUser 包含用户信息的请求体
     * @return 创建成功的用户对象及HTTP 201状态码
     * @throws IllegalArgumentException 当用户名已存在时抛出
     */
    @PostMapping  // POST /users
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BizUser> create(@RequestBody BizUser bizUser) {
        return new ResponseEntity<>(bizUserService.create(bizUser), HttpStatus.CREATED);
    }

    /**
     * 创建用户及其详细信息
     * @param request 包含用户和详细信息的请求体
     * @return 创建成功的用户对象及HTTP 201状态码
     */
    @PostMapping("/with-info")  // POST /users/with-info
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BizUser> createWithUserInfo(@RequestBody Map<String, Object> request) {
        BizUser bizUser = mapToUser(Collections.unmodifiableMap((Map<String, Object>) request.get("user")));
        BizUserInfo bizUserInfo = mapToUserInfo(Collections.unmodifiableMap((Map<String, Object>) request.get("bizUserInfo")));
        
        return new ResponseEntity<>(bizUserService.createWithUserInfo(bizUser, bizUserInfo), HttpStatus.CREATED);
    }

    /**
     * 更新指定ID的用户信息
     * @param userId 用户ID
     * @param bizUser 包含更新后用户信息的请求体
     * @return 更新成功的用户对象及HTTP 200状态码
     * @throws EntityNotFoundException 当指定ID的用户不存在时抛出
     */
    @PutMapping("/{userId}")  // PUT /users/{userId}
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BizUser> update(@PathVariable Integer userId, @RequestBody BizUser bizUser) {
        return ResponseEntity.ok(bizUserService.update(userId, bizUser));
    }

    /**
     * 删除指定ID的用户记录
     * @param userId 用户ID
     * @return HTTP 204无内容状态码，表示删除成功
     * @throws EntityNotFoundException 当指定ID的用户不存在时抛出
     */
    @DeleteMapping("/{userId}")  // DELETE /users/{userId}
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Integer userId) {
        bizUserService.delete(userId);
        return ResponseEntity.noContent().build();
    }

    // ==================== 权限和状态管理 ====================

    /**
     * 根据权限级别查询用户列表
     * @param permissionLevel 权限级别
     * @return 指定权限级别的用户列表及HTTP 200状态码
     */
    @GetMapping("/permission/{permissionLevel}")  // GET /users/permission/{permissionLevel}
    public ResponseEntity<List<BizUser>> getByPermissionLevel(@PathVariable Integer permissionLevel) {
        return ResponseEntity.ok(bizUserService.getByPermissionLevel(permissionLevel));
    }

    /**
     * 根据账户状态查询用户列表
     * @param status 账户状态
     * @return 指定状态的用户列表及HTTP 200状态码
     */
    @GetMapping("/status/{status}")  // GET /users/status/{status}
    public ResponseEntity<List<BizUser>> getByStatus(@PathVariable Integer status) {
        return ResponseEntity.ok(bizUserService.getByStatus(status));
    }

    /**
     * 更新用户最后登录时间
     * @param userId 用户ID
     * @return HTTP 200状态码，表示更新成功
     */
    @PostMapping("/{userId}/update-login-time")  // POST /users/{userId}/update-login-time
    public ResponseEntity<Void> updateLastLoginTime(@PathVariable Integer userId) {
        bizUserService.updateLastLoginTime(userId);
        return ResponseEntity.ok().build();
    }

    /**
     * 修改密码
     * @param userId 用户ID
     * @param request 包含旧密码和新密码的请求体
     * @return 修改结果及HTTP 200状态码
     */
    @PostMapping("/{userId}/change-password")  // POST /users/{userId}/change-password
    public ResponseEntity<Map<String, Object>> changePassword(@PathVariable Integer userId, 
                                                        @RequestBody Map<String, String> request) {
        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");
        
        boolean success = bizUserService.changePassword(userId, oldPassword, newPassword);
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", success ? "密码修改成功" : "密码修改失败");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 重置密码（管理员操作）
     * @param userId 用户ID
     * @param request 包含新密码的请求体
     * @return 重置结果及HTTP 200状态码
     */
    @PostMapping("/{userId}/reset-password")  // POST /users/{userId}/reset-password
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> resetPassword(@PathVariable Integer userId, 
                                                       @RequestBody Map<String, String> request) {
        String newPassword = request.get("newPassword");
        
        boolean success = bizUserService.resetPassword(userId, newPassword);
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", success ? "密码重置成功" : "密码重置失败");
        
        return ResponseEntity.ok(response);
    }

    // ==================== 用户详细信息管理 ====================

    /**
     * 根据用户ID获取详细信息
     * @param userId 用户ID
     * @return 用户详细信息对象及HTTP 200状态码
     * @throws EntityNotFoundException 当指定用户的详细信息不存在时抛出
     */
    @GetMapping("/{userId}/info")  // GET /users/{userId}/info
    public ResponseEntity<BizUserInfo> getUserInfo(@PathVariable Integer userId) {
        return ResponseEntity.ok(bizUserInfoService.getByUserId(userId));
    }

    /**
     * 更新用户详细信息
     * @param userId 用户ID
     * @param bizUserInfo 包含更新后详细信息的请求体
     * @return 更新成功的用户详细信息对象及HTTP 200状态码
     * @throws EntityNotFoundException 当指定用户的详细信息不存在时抛出
     */
    @PutMapping("/{userId}/info")  // PUT /users/{userId}/info
    public ResponseEntity<BizUserInfo> updateUserInfo(@PathVariable Integer userId, @RequestBody BizUserInfo bizUserInfo) {
        return ResponseEntity.ok(bizUserInfoService.update(userId, bizUserInfo));
    }

    /**
     * 部分更新用户详细信息
     * @param userId 用户ID
     * @param bizUserInfo 包含部分更新信息的请求体
     * @return 更新成功的用户详细信息对象及HTTP 200状态码
     * @throws EntityNotFoundException 当指定用户的详细信息不存在时抛出
     */
    @PatchMapping("/{userId}/info")  // PATCH /users/{userId}/info
    public ResponseEntity<BizUserInfo> patchUserInfo(@PathVariable Integer userId, @RequestBody BizUserInfo bizUserInfo) {
        return ResponseEntity.ok(bizUserInfoService.patch(userId, bizUserInfo));
    }

    /**
     * 删除用户详细信息
     * @param userId 用户ID
     * @return HTTP 204无内容状态码，表示删除成功
     * @throws EntityNotFoundException 当指定用户的详细信息不存在时抛出
     */
    @DeleteMapping("/{userId}/info")  // DELETE /users/{userId}/info
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUserInfo(@PathVariable Integer userId) {
        bizUserInfoService.deleteByUserId(userId);
        return ResponseEntity.noContent().build();
    }

    // ==================== 关联借款人管理 ====================

    /**
     * 根据用户ID获取关联的借款人列表
     * @param userId 用户ID
     * @return 关联的借款人列表及HTTP 200状态码
     */
    @GetMapping("/{userId}/borrowers")  // GET /users/{userId}/borrowers
    public ResponseEntity<List<Borrowers>> getUserBorrowers(@PathVariable Integer userId) {
        return ResponseEntity.ok(borrowersService.getByUserId(userId));
    }

    /**
     * 根据用户ID获取关联借款人的完整信息
     * @param userId 用户ID
     * @return 包含完整信息的借款人列表及HTTP 200状态码
     */
    @GetMapping("/{userId}/borrowers/full-details")  // GET /users/{userId}/borrowers/full-details
    public ResponseEntity<List<Borrowers>> getUserBorrowersWithFullDetails(@PathVariable Integer userId) {
        return ResponseEntity.ok(borrowersService.getBorrowersWithFullDetailsByUserId(userId));
    }

    // ==================== 搜索和统计 ====================

    /**
     * 搜索用户（根据用户名或真实姓名）
     * @param keyword 搜索关键词（查询参数）
     * @return 匹配的用户列表及HTTP 200状态码
     */
    @GetMapping("/search")  // GET /users/search?keyword=张
    public ResponseEntity<List<BizUser>> searchUsers(@RequestParam String keyword) {
        return ResponseEntity.ok(bizUserService.searchUsers(keyword));
    }

    /**
     * 获取活跃用户列表
     * @return 活跃用户列表及HTTP 200状态码
     */
    @GetMapping("/active")  // GET /users/active
    public ResponseEntity<List<BizUser>> getActiveUsers() {
        return ResponseEntity.ok(bizUserService.getActiveUsers());
    }

    /**
     * 检查用户是否存在
     * @param userId 用户ID
     * @return 检查结果及HTTP 200状态码
     */
    @GetMapping("/{userId}/exists")  // GET /users/{userId}/exists
    public ResponseEntity<Boolean> checkUserExists(@PathVariable Integer userId) {
        return ResponseEntity.ok(bizUserService.existsById(userId));
    }

    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return 检查结果及HTTP 200状态码
     */
    @GetMapping("/username/{username}/exists")  // GET /users/username/{username}/exists
    public ResponseEntity<Boolean> checkUsernameExists(@PathVariable String username) {
        return ResponseEntity.ok(bizUserService.existsByUsername(username));
    }

    // ==================== 辅助方法 ====================

    /**
     * 将Map转换为User对象
     */
    private BizUser mapToUser(Map<String, Object> userMap) {
        if (userMap == null) return null;
        
        return BizUser.builder()
                .username((String) userMap.get("username"))
                .passwordHash((String) userMap.get("passwordHash"))
                .permissionLevel((Integer) userMap.get("permissionLevel"))
                .status((Integer) userMap.get("status"))
                .build();
    }

    /**
     * 将Map转换为UserInfo对象
     */
    private BizUserInfo mapToUserInfo(Map<String, Object> userInfoMap) {
        if (userInfoMap == null) return null;
        
        return BizUserInfo.builder()
                .realName((String) userInfoMap.get("realName"))
                .phone((String) userInfoMap.get("phone"))
                .email((String) userInfoMap.get("email"))
                .address((String) userInfoMap.get("address"))
                .gender((String) userInfoMap.get("gender"))
                .avatarUrl((String) userInfoMap.get("avatarUrl"))
                .seatNumber((String) userInfoMap.get("seatNumber"))
                .department((String) userInfoMap.get("department"))
                .directLeaderId((Integer) userInfoMap.get("directLeaderId"))
                .position((String) userInfoMap.get("position"))
                .team((String) userInfoMap.get("team"))
                .status((Integer) userInfoMap.get("status"))
                .build();
    }

}