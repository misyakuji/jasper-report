package com.misyakuji.service;

import com.misyakuji.entity.BizUser;
import com.misyakuji.entity.BizUserInfo;
import com.misyakuji.entity.Borrowers;
import com.misyakuji.repository.BizUserRepository;
import com.misyakuji.repository.BizUserInfoRepository;
import com.misyakuji.repository.BorrowersRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户服务层实现类
 * 提供用户数据的业务逻辑处理，包括增删改查和权限管理等功能
 * 
 * @since v2.0 新增用户管理功能
 */
@Service
@Transactional
public class BizUserService {

    private final BizUserRepository bizUserRepository;
    private final BizUserInfoRepository bizUserInfoRepository;
    private final BorrowersRepository borrowersRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 构造函数，通过依赖注入获取组件
     * @param bizUserRepository 用户数据访问层
     * @param bizUserInfoRepository 用户详细信息数据访问层
     * @param borrowersRepository 借款人数据访问层
     * @param passwordEncoder 密码编码器
     */
    @Autowired
    public BizUserService(BizUserRepository bizUserRepository,
                          BizUserInfoRepository bizUserInfoRepository,
                          BorrowersRepository borrowersRepository,
                          PasswordEncoder passwordEncoder) {
        this.bizUserRepository = bizUserRepository;
        this.bizUserInfoRepository = bizUserInfoRepository;
        this.borrowersRepository = borrowersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 查询所有用户信息
     * @return 包含所有用户的列表
     */
    public List<BizUser> getAll() {
        return bizUserRepository.findAll();
    }

    /**
     * 查询所有用户信息（包含详细信息）
     * @return 包含所有用户及其详细信息的列表
     */
    public List<BizUser> getAllWithUserInfo() {
        return bizUserRepository.findAllWithUserInfo();
    }

    /**
     * 根据ID查询用户信息
     * @param userId 用户ID
     * @return 查询到的用户对象
     * @throws EntityNotFoundException 当指定ID的用户不存在时抛出
     */
    public BizUser getById(Integer userId) {
        return bizUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("BizUser not found with id: " + userId));
    }

    /**
     * 根据ID查询用户信息（包含详细信息）
     * @param userId 用户ID
     * @return 查询到的用户对象（包含详细信息）
     * @throws EntityNotFoundException 当指定ID的用户不存在时抛出
     */
    public BizUser getByIdWithUserInfo(Integer userId) {
        return bizUserRepository.findByIdWithUserInfo(userId)
                .orElseThrow(() -> new EntityNotFoundException("BizUser not found with id: " + userId));
    }

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 查询到的用户对象
     */
    public Optional<BizUser> getByUsername(String username) {
        return bizUserRepository.findByUsername(username);
    }

    /**
     * 创建新用户
     * @param bizUser 用户对象
     * @return 保存后的用户对象
     * @throws IllegalArgumentException 当用户名已存在时抛出
     */
    public BizUser create(BizUser bizUser) {
        // 检查用户名是否已存在
        if (bizUserRepository.countByUsername(bizUser.getUsername()) > 0) {
            throw new IllegalArgumentException("Username already exists: " + bizUser.getUsername());
        }
        
        // 加密密码
        if (bizUser.getPasswordHash() != null && !bizUser.getPasswordHash().isEmpty()) {
            bizUser.setPasswordHash(passwordEncoder.encode(bizUser.getPasswordHash()));
        }
        
        // 设置默认值
        if (bizUser.getPermissionLevel() == null) {
            bizUser.setPermissionLevel(1); // 默认为普通用户
        }
        if (bizUser.getStatus() == null) {
            bizUser.setStatus(1); // 默认为正常状态
        }
        
        return bizUserRepository.save(bizUser);
    }

    /**
     * 创建用户及其详细信息
     * @param bizUser 用户对象
     * @param bizUserInfo 用户详细信息
     * @return 保存后的用户对象
     */
    public BizUser createWithUserInfo(BizUser bizUser, BizUserInfo bizUserInfo) {
        // 创建用户
        BizUser savedBizUser = create(bizUser);
        
        // 设置用户详细信息的关联
        bizUserInfo.setBizUser(savedBizUser);
        bizUserInfoRepository.save(bizUserInfo);
        
        // 重新查询以获取完整信息
        return getByIdWithUserInfo(savedBizUser.getUserId());
    }

    /**
     * 更新用户信息
     * @param userId 用户ID
     * @param bizUser 包含更新信息的用户对象
     * @return 更新后的用户对象
     * @throws EntityNotFoundException 当指定ID的用户不存在时抛出
     */
    public BizUser update(Integer userId, BizUser bizUser) {
        BizUser existingBizUser = getById(userId);
        
        // 更新字段
        if (bizUser.getUsername() != null && !bizUser.getUsername().equals(existingBizUser.getUsername())) {
            // 检查新用户名是否已存在
            if (bizUserRepository.countByUsername(bizUser.getUsername()) > 0) {
                throw new IllegalArgumentException("Username already exists: " + bizUser.getUsername());
            }
            existingBizUser.setUsername(bizUser.getUsername());
        }
        
        if (bizUser.getPasswordHash() != null && !bizUser.getPasswordHash().isEmpty()) {
            existingBizUser.setPasswordHash(passwordEncoder.encode(bizUser.getPasswordHash()));
        }
        
        if (bizUser.getPermissionLevel() != null) {
            existingBizUser.setPermissionLevel(bizUser.getPermissionLevel());
        }
        
        if (bizUser.getStatus() != null) {
            existingBizUser.setStatus(bizUser.getStatus());
        }
        
        return bizUserRepository.save(existingBizUser);
    }

    /**
     * 删除用户（级联删除详细信息，借款人关联设为NULL）
     * @param userId 用户ID
     * @throws EntityNotFoundException 当指定ID的用户不存在时抛出
     */
    public void delete(Integer userId) {
        BizUser bizUser = getById(userId);
        
        // 检查是否有关联的借款人
        List<Borrowers> linkedBorrowers = borrowersRepository.findByBizUser(bizUser);
        if (!linkedBorrowers.isEmpty()) {
            // 取消所有借款人关联
            linkedBorrowers.forEach(borrower -> borrower.setBizUser(null));
            borrowersRepository.saveAll(linkedBorrowers);
        }
        
        // 删除用户（会级联删除详细信息）
        bizUserRepository.delete(bizUser);
    }

    /**
     * 根据权限级别查询用户列表
     * @param permissionLevel 权限级别
     * @return 指定权限级别的用户列表
     */
    public List<BizUser> getByPermissionLevel(Integer permissionLevel) {
        return bizUserRepository.findByPermissionLevel(permissionLevel);
    }

    /**
     * 根据账户状态查询用户列表
     * @param status 账户状态
     * @return 指定状态的用户列表
     */
    public List<BizUser> getByStatus(Integer status) {
        return bizUserRepository.findByStatus(status);
    }

    /**
     * 更新用户最后登录时间
     * @param userId 用户ID
     */
    public void updateLastLoginTime(Integer userId) {
        BizUser bizUser = getById(userId);
        bizUser.setLastLoginTime(LocalDateTime.now());
        bizUserRepository.save(bizUser);
    }

    /**
     * 修改密码
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否修改成功
     * @throws EntityNotFoundException 当用户不存在时抛出
     * @throws IllegalArgumentException 当旧密码不正确时抛出
     */
    public boolean changePassword(Integer userId, String oldPassword, String newPassword) {
        BizUser bizUser = getById(userId);
        
        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, bizUser.getPasswordHash())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }
        
        // 设置新密码
        bizUser.setPasswordHash(passwordEncoder.encode(newPassword));
        bizUserRepository.save(bizUser);
        
        return true;
    }

    /**
     * 重置密码（管理员操作）
     * @param userId 用户ID
     * @param newPassword 新密码
     * @return 是否重置成功
     * @throws EntityNotFoundException 当用户不存在时抛出
     */
    public boolean resetPassword(Integer userId, String newPassword) {
        BizUser bizUser = getById(userId);
        bizUser.setPasswordHash(passwordEncoder.encode(newPassword));
        bizUserRepository.save(bizUser);
        return true;
    }

    /**
     * 搜索用户（根据用户名或真实姓名）
     * @param keyword 搜索关键词
     * @return 匹配的用户列表
     */
    public List<BizUser> searchUsers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAll();
        }
        return bizUserRepository.searchBizUsers(keyword.trim());
    }

    /**
     * 获取活跃用户列表
     * @return 活跃用户列表
     */
    public List<BizUser> getActiveUsers() {
        return bizUserRepository.findActiveBizUsers();
    }

    /**
     * 检查用户是否存在
     * @param userId 用户ID
     * @return 是否存在
     */
    public boolean existsById(Integer userId) {
        return bizUserRepository.findById(userId).isPresent();
    }

    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return 是否存在
     */
    public boolean existsByUsername(String username) {
        return bizUserRepository.countByUsername(username) > 0;
    }

    // ==================== 兼容性方法 - 保持向后兼容 ====================

    /**
     * 向后兼容：根据ID查询用户信息（Long类型）
     * @param id 用户ID（Long类型）
     * @return 查询到的用户对象
     */
    @Deprecated
    public BizUser getById(Long id) {
        return getById(id != null ? id.intValue() : null);
    }

    /**
     * 向后兼容：创建用户（使用旧的用户对象结构）
     * @param bizUser 用户对象（包含id、username、password、role字段）
     * @return 保存后的用户对象
     */
    @Deprecated
    public BizUser createLegacyUser(BizUser bizUser) {
        BizUser newBizUser = BizUser.builder()
                .username(bizUser.getUsername())
                .passwordHash(bizUser.getPasswordHash())
                .permissionLevel(bizUser.getPermissionLevel())
                .build();
        
        return create(newBizUser);
    }
}