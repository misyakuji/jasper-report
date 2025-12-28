package com.misyakuji.repository;

import com.misyakuji.entity.BizUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 用户数据访问层接口
 * 提供对biz_user表的CRUD操作和复杂查询功能
 * 
 * @since v2.0 新增用户实体Repository
 */
public interface BizUserRepository extends JpaRepository<BizUser, Integer>, JpaSpecificationExecutor<BizUser> {

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户对象
     */
    Optional<BizUser> findByUsername(String username);

    /**
     * 根据用户ID查询用户（为了JPA关系查询兼容）
     * @param userId 用户ID
     * @return 用户对象
     */
    Optional<BizUser> findByUserId(Integer userId);

    /**
     * 根据权限级别查询用户列表
     * @param permissionLevel 权限级别
     * @return 指定权限级别的用户列表
     */
    List<BizUser> findByPermissionLevel(Integer permissionLevel);

    /**
     * 根据账户状态查询用户列表
     * @param status 账户状态
     * @return 指定状态的用户列表
     */
    List<BizUser> findByStatus(Integer status);

    /**
     * 根据权限级别和状态查询用户列表
     * @param permissionLevel 权限级别
     * @param status 账户状态
     * @return 符合条件的用户列表
     */
    List<BizUser> findByPermissionLevelAndStatus(Integer permissionLevel, Integer status);

    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return 存在的记录数
     */
    long countByUsername(String username);

    /**
     * 根据权限级别和状态查询用户数量
     * @param permissionLevel 权限级别
     * @param status 账户状态
     * @return 符合条件的用户数量
     */
    long countByPermissionLevelAndStatus(Integer permissionLevel, Integer status);

    /**
     * 获取包含用户详细信息的用户列表
     * @return 包含详细信息的用户列表
     */
    @Query("SELECT u FROM BizUser u LEFT JOIN FETCH u.bizUserInfo")
    List<BizUser> findAllWithUserInfo();

    /**
     * 根据ID获取包含详细信息的用户
     * @param userId 用户ID
     * @return 包含详细信息的用户对象
     */
    @Query("SELECT u FROM BizUser u LEFT JOIN FETCH u.bizUserInfo WHERE u.userId = :userId")
    Optional<BizUser> findByIdWithUserInfo(@Param("userId") Integer userId);

    /**
     * 根据权限级别获取包含详细信息的用户列表
     * @param permissionLevel 权限级别
     * @return 包含详细信息的用户列表
     */
    @Query("SELECT u FROM BizUser u LEFT JOIN FETCH u.bizUserInfo WHERE u.permissionLevel = :permissionLevel")
    List<BizUser> findByPermissionLevelWithUserInfo(@Param("permissionLevel") Integer permissionLevel);

    /**
     * 获取活跃用户列表（状态为正常且有最后登录时间）
     * @return 活跃用户列表
     */
    @Query("SELECT u FROM BizUser u WHERE u.status = 1 AND u.lastLoginTime IS NOT NULL ORDER BY u.lastLoginTime DESC")
    List<BizUser> findActiveBizUsers();

    /**
     * 搜索用户（根据用户名或真实姓名）
     * @param keyword 搜索关键词
     * @return 匹配的用户列表
     */
    @Query("SELECT u FROM BizUser u LEFT JOIN u.bizUserInfo ui WHERE " +
           "u.username LIKE %:keyword% OR ui.realName LIKE %:keyword%")
    List<BizUser> searchBizUsers(@Param("keyword") String keyword);

    /**
     * 删除用户（根据用户ID）
     * @param id 用户ID
     */
    @Modifying
    @Query("DELETE FROM BizUser u WHERE u.userId = :id")
    void deleteByBizUserId(@Param("id") Integer id);
}