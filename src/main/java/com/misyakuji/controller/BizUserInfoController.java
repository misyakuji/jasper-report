package com.misyakuji.controller;

import com.misyakuji.common.ApiResponse;
import com.misyakuji.entity.BizUser;
import com.misyakuji.entity.BizUserInfo;
import com.misyakuji.service.BizUserInfoService;
import com.misyakuji.service.BizUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 用户详细信息管理控制器
 * 提供用户详细信息的增删改查等RESTful API接口
 * 
 * @since v2.0 重构为使用新的Service层和实体结构
 */
@RestController
@RequestMapping("/user-info")
@CrossOrigin(origins = "*", maxAge = 3600)
public class BizUserInfoController {

    private final BizUserInfoService bizUserInfoService;
    private final BizUserService bizUserService;

    /**
     * 构造函数，通过依赖注入获取服务实例
     * @param bizUserInfoService 用户详细信息服务层组件
     * @param bizUserService 用户服务层组件
     */
    @Autowired
    public BizUserInfoController(BizUserInfoService bizUserInfoService, BizUserService bizUserService) {
        this.bizUserInfoService = bizUserInfoService;
        this.bizUserService = bizUserService;
    }

    /**
     * 获取当前登录用户的详细信息
     * @param authentication 认证信息
     * @return 包含用户详细信息的响应
     */
    @GetMapping("/current")  // GET /bizUser-info/current
    public ApiResponse<Map<String, Object>> getCurrentUserInfo(Authentication authentication) {
        // 从Authentication中获取用户名
        String username = authentication.getName();
        
        // 根据用户名查询用户信息
        Optional<BizUser> userOpt = bizUserService.getByUsername(username);
        if (userOpt.isEmpty()) {
            return ApiResponse.error("用户不存在");
        }
        
        BizUser bizUser = userOpt.get();
        
        // 根据用户ID查询用户详情
        Optional<BizUserInfo> userInfoOpt = bizUserInfoService.getByUser(bizUser);
        
        Map<String, Object> result = new HashMap<>();
        result.put("user", bizUser);
        result.put("userInfo", userInfoOpt.orElse(getDefaultUserInfo(bizUser.getUserId())));
        
        return ApiResponse.success(result);
    }

    /**
     * 查询所有用户详细信息
     * @return 包含所有用户详细信息的列表及HTTP 200状态码
     */
    @GetMapping  // GET /bizUser-info
    public ResponseEntity<List<BizUserInfo>> getAll() {
        return ResponseEntity.ok(bizUserInfoService.getAll());
    }

    /**
     * 查询所有用户详细信息（包含用户信息）
     * @return 包含用户信息的详细信息列表及HTTP 200状态码
     */
    @GetMapping("/with-user")  // GET /bizUser-info/with-bizUser
    public ResponseEntity<List<BizUserInfo>> getAllWithUser() {
        return ResponseEntity.ok(bizUserInfoService.getAllWithUser());
    }

    /**
     * 根据用户ID查询用户详细信息
     * @param userId 用户ID
     * @return 用户详细信息对象及HTTP 200状态码
     */
    @GetMapping("/user/{userId}")  // GET /bizUser-info/bizUser/{userId}
    public ResponseEntity<BizUserInfo> getByUserId(@PathVariable Integer userId) {
        try {
            return ResponseEntity.ok(bizUserInfoService.getByUserId(userId));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 根据手机号码查询用户详细信息
     * @param phone 手机号码
     * @return 用户详细信息对象及HTTP 200状态码
     */
    @GetMapping("/phone/{phone}")  // GET /bizUser-info/phone/{phone}
    public ResponseEntity<BizUserInfo> getByPhone(@PathVariable String phone) {
        return bizUserInfoService.getByPhone(phone)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 根据邮箱查询用户详细信息
     * @param email 邮箱
     * @return 用户详细信息对象及HTTP 200状态码
     */
    @GetMapping("/email/{email}")  // GET /bizUser-info/email/{email}
    public ResponseEntity<BizUserInfo> getByEmail(@PathVariable String email) {
        return bizUserInfoService.getByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 创建用户详细信息
     * @param bizUserInfo 包含用户详细信息的请求体
     * @return 创建成功的用户详细信息对象及HTTP 201状态码
     */
    @PostMapping  // POST /bizUser-info
    public ResponseEntity<BizUserInfo> create(@RequestBody BizUserInfo bizUserInfo) {
        return new ResponseEntity<>(bizUserInfoService.create(bizUserInfo), org.springframework.http.HttpStatus.CREATED);
    }

    /**
     * 更新指定用户ID的详细信息
     * @param userId 用户ID
     * @param bizUserInfo 包含更新后用户详细信息的请求体
     * @return 更新成功的用户详细信息对象及HTTP 200状态码
     */
    @PutMapping("/user/{userId}")  // PUT /bizUser-info/bizUser/{userId}
    public ResponseEntity<BizUserInfo> update(@PathVariable Integer userId, @RequestBody BizUserInfo bizUserInfo) {
        try {
            return ResponseEntity.ok(bizUserInfoService.update(userId, bizUserInfo));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 部分更新指定用户ID的详细信息
     * @param userId 用户ID
     * @param bizUserInfo 包含部分更新信息的请求体
     * @return 更新成功的用户详细信息对象及HTTP 200状态码
     */
    @PatchMapping("/user/{userId}")  // PATCH /bizUser-info/bizUser/{userId}
    public ResponseEntity<BizUserInfo> patch(@PathVariable Integer userId, @RequestBody BizUserInfo bizUserInfo) {
        try {
            return ResponseEntity.ok(bizUserInfoService.patch(userId, bizUserInfo));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 删除指定用户ID的详细信息
     * @param userId 用户ID
     * @return HTTP 204无内容状态码，表示删除成功
     */
    @DeleteMapping("/user/{userId}")  // DELETE /bizUser-info/bizUser/{userId}
    public ResponseEntity<Void> deleteByUserId(@PathVariable Integer userId) {
        try {
            bizUserInfoService.deleteByUserId(userId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ==================== 查询操作 ====================

    /**
     * 根据部门查询用户详细信息列表
     * @param department 部门名称
     * @return 指定部门的用户详细信息列表及HTTP 200状态码
     */
    @GetMapping("/department/{department}")  // GET /bizUser-info/department/{department}
    public ResponseEntity<List<BizUserInfo>> getByDepartment(@PathVariable String department) {
        return ResponseEntity.ok(bizUserInfoService.getByDepartment(department));
    }

    /**
     * 根据职位查询用户详细信息列表
     * @param position 职位名称
     * @return 指定职位的用户详细信息列表及HTTP 200状态码
     */
    @GetMapping("/position/{position}")  // GET /bizUser-info/position/{position}
    public ResponseEntity<List<BizUserInfo>> getByPosition(@PathVariable String position) {
        return ResponseEntity.ok(bizUserInfoService.getByPosition(position));
    }

    /**
     * 根据团队查询用户详细信息列表
     * @param team 团队名称
     * @return 指定团队的用户详细信息列表及HTTP 200状态码
     */
    @GetMapping("/team/{team}")  // GET /bizUser-info/team/{team}
    public ResponseEntity<List<BizUserInfo>> getByTeam(@PathVariable String team) {
        return ResponseEntity.ok(bizUserInfoService.getByTeam(team));
    }

    /**
     * 根据信息状态查询用户详细信息列表
     * @param status 信息状态（1-正常，0-不可用）
     * @return 指定状态的用户详细信息列表及HTTP 200状态码
     */
    @GetMapping("/status/{status}")  // GET /bizUser-info/status/{status}
    public ResponseEntity<List<BizUserInfo>> getByStatus(@PathVariable Integer status) {
        return ResponseEntity.ok(bizUserInfoService.getByStatus(status));
    }

    /**
     * 根据直属领导ID查询下属信息
     * @param directLeaderId 直属领导用户ID
     * @return 该领导的下属信息列表及HTTP 200状态码
     */
    @GetMapping("/leader/{directLeaderId}")  // GET /bizUser-info/leader/{directLeaderId}
    public ResponseEntity<List<BizUserInfo>> getByDirectLeaderId(@PathVariable Integer directLeaderId) {
        return ResponseEntity.ok(bizUserInfoService.getByDirectLeaderId(directLeaderId));
    }

    /**
     * 根据真实姓名模糊查询
     * @param realName 真实姓名（支持模糊匹配）
     * @return 匹配的用户详细信息列表及HTTP 200状态码
     */
    @GetMapping("/search/{realName}")  // GET /bizUser-info/search/{realName}
    public ResponseEntity<List<BizUserInfo>> getByRealNameContaining(@PathVariable String realName) {
        return ResponseEntity.ok(bizUserInfoService.getByRealNameContaining(realName));
    }

    // ==================== 业务操作 ====================

    /**
     * 搜索用户详细信息
     * @param department 部门（查询参数，可选）
     * @param position 职位（查询参数，可选）
     * @param status 状态（查询参数，可选）
     * @return 符合条件的用户详细信息列表及HTTP 200状态码
     */
    @GetMapping("/search")  // GET /bizUser-info/search?department=技术部&position=工程师&status=1
    public ResponseEntity<List<BizUserInfo>> searchByMultipleConditions(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) Integer status) {
        return ResponseEntity.ok(bizUserInfoService.searchByMultipleConditions(department, position, status));
    }

    /**
     * 统计各部门人数
     * @return 按部门分组的用户数量统计及HTTP 200状态码
     */
    @GetMapping("/stats/department")  // GET /bizUser-info/stats/department
    public ResponseEntity<List<Object[]>> countUsersByDepartment() {
        return ResponseEntity.ok(bizUserInfoService.countUsersByDepartment());
    }

    /**
     * 统计各职位人数
     * @return 按职位分组的用户数量统计及HTTP 200状态码
     */
    @GetMapping("/stats/position")  // GET /bizUser-info/stats/position
    public ResponseEntity<List<Object[]>> countUsersByPosition() {
        return ResponseEntity.ok(bizUserInfoService.countUsersByPosition());
    }

    /**
     * 获取指定领导的下属统计信息
     * @param directLeaderId 领导用户ID
     * @return 下属统计信息及HTTP 200状态码
     */
    @GetMapping("/stats/leader/{directLeaderId}")  // GET /bizUser-info/stats/leader/{directLeaderId}
    public ResponseEntity<List<Object[]>> countSubordinatesByPosition(@PathVariable Integer directLeaderId) {
        return ResponseEntity.ok(bizUserInfoService.countSubordinatesByPosition(directLeaderId));
    }

    // ==================== 辅助方法 ====================

    /**
     * 获取默认用户详细信息
     * @param userId 用户ID
     * @return 默认用户详细信息对象
     */
    private BizUserInfo getDefaultUserInfo(Integer userId) {
        return BizUserInfo.builder()
                .bizUser(BizUser.builder().userId(userId).build())
                .build();
    }
}