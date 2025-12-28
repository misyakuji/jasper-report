package com.misyakuji.repository;

import com.misyakuji.entity.BizUserInfo;
import com.misyakuji.entity.BizUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 用户详细信息数据访问层接口
 * 提供对user_info表的CRUD操作和复杂查询功能
 * 
 * @since v2.0 新增用户详细信息Repository
 */
public interface BizUserInfoRepository extends JpaRepository<BizUserInfo, Integer>, JpaSpecificationExecutor<BizUserInfo> {

    /**
     * 根据用户ID查询用户详细信息
     * @param userId 用户ID
     * @return 用户详细信息对象
     */
    Optional<BizUserInfo> findByBizUserUserId(Integer userId);

    /**
     * 根据用户查询用户详细信息
     * @param bizUser 用户对象
     * @return 用户详细信息对象
     */
    Optional<BizUserInfo> findByBizUser(BizUser bizUser);

    /**
     * 根据手机号码查询用户详细信息
     * @param phone 手机号码
     * @return 用户详细信息对象
     */
    Optional<BizUserInfo> findByPhone(String phone);

    /**
     * 根据邮箱查询用户详细信息
     * @param email 邮箱
     * @return 用户详细信息对象
     */
    Optional<BizUserInfo> findByEmail(String email);

    /**
     * 根据部门查询用户详细信息列表
     * @param department 部门名称
     * @return 指定部门的用户详细信息列表
     */
    List<BizUserInfo> findByDepartment(String department);

    /**
     * 根据职位查询用户详细信息列表
     * @param position 职位名称
     * @return 指定职位的用户详细信息列表
     */
    List<BizUserInfo> findByPosition(String position);

    /**
     * 根据团队查询用户详细信息列表
     * @param team 团队名称
     * @return 指定团队的用户详细信息列表
     */
    List<BizUserInfo> findByTeam(String team);

    /**
     * 根据信息状态查询用户详细信息列表
     * @param status 信息状态（1-正常，0-离职）
     * @return 指定状态的用户详细信息列表
     */
    List<BizUserInfo> findByStatus(Integer status);

    /**
     * 根据直属领导ID查询下属信息
     * @param directLeaderId 直属领导用户ID
     * @return 该领导的下属信息列表
     */
    List<BizUserInfo> findByDirectLeaderId(Integer directLeaderId);

    /**
     * 根据真实姓名模糊查询
     * @param realName 真实姓名（支持模糊匹配）
     * @return 匹配的用户详细信息列表
     */
    @Query("SELECT ui FROM BizUserInfo ui WHERE ui.realName LIKE %:realName%")
    List<BizUserInfo> findByRealNameContaining(@Param("realName") String realName);

    /**
     * 根据真实姓名模糊查询（兼容性方法）
     * @param realName 真实姓名（支持模糊匹配）
     * @return 匹配的用户详细信息列表
     */
    List<BizUserInfo> getByRealNameContaining(String realName);

    /**
     * 根据多个条件查询用户详细信息
     * @param department 部门（可为空）
     * @param position 职位（可为空）
     * @param status 状态（可为空）
     * @return 符合条件的用户详细信息列表
     */
    @Query("SELECT ui FROM BizUserInfo ui WHERE " +
           "(:department IS NULL OR ui.department = :department) AND " +
           "(:position IS NULL OR ui.position = :position) AND " +
           "(:status IS NULL OR ui.status = :status)")
    List<BizUserInfo> findByMultipleConditions(@Param("department") String department,
                                               @Param("position") String position,
                                               @Param("status") Integer status);

    /**
     * 根据部门、职位和状态查询用户详细信息
     * @param department 部门（可为空）
     * @param position 职位（可为空）
     * @param status 状态（可为空）
     * @return 符合条件的用户详细信息列表
     */
    @Query("SELECT ui FROM BizUserInfo ui WHERE " +
           "(:department IS NULL OR ui.department = :department) AND " +
           "(:position IS NULL OR ui.position = :position) AND " +
           "(:status IS NULL OR ui.status = :status)")
    List<BizUserInfo> findByDepartmentAndPositionAndStatus(@Param("department") String department,
                                                           @Param("position") String position,
                                                           @Param("status") Integer status);

    /**
     * 获取包含用户信息的用户详细信息列表
     * @return 包含用户信息的详细信息列表
     */
    @Query("SELECT ui FROM BizUserInfo ui LEFT JOIN FETCH ui.bizUser")
    List<BizUserInfo> findAllWithBizUser();

    /**
     * 根据部门获取包含用户信息的用户详细信息列表
     * @param department 部门名称
     * @return 指定部门的详细信息列表（包含用户信息）
     */
    @Query("SELECT ui FROM BizUserInfo ui LEFT JOIN FETCH ui.bizUser WHERE ui.department = :department")
    List<BizUserInfo> findByDepartmentWithUser(@Param("department") String department);

    /**
     * 统计各部门人数
     * @return 按部门分组的用户数量统计
     */
    @Query("SELECT ui.department, COUNT(*) FROM BizUserInfo ui WHERE ui.status = 1 GROUP BY ui.department")
    List<Object[]> countUsersByDepartment();

    /**
     * 统计各职位人数
     * @return 按职位分组的用户数量统计
     */
    @Query("SELECT ui.position, COUNT(*) FROM BizUserInfo ui WHERE ui.status = 1 GROUP BY ui.position")
    List<Object[]> countUsersByPosition();

    /**
     * 获取指定领导的下属统计信息
     * @param directLeaderId 领导用户ID
     * @return 下属统计信息
     */
    @Query("SELECT ui.position, COUNT(*) FROM BizUserInfo ui WHERE ui.directLeaderId = :directLeaderId AND ui.status = 1 GROUP BY ui.position")
    List<Object[]> countSubordinatesByPosition(@Param("directLeaderId") Integer directLeaderId);

}