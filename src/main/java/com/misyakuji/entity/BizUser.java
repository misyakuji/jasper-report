package com.misyakuji.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户账户实体类
 * 映射到数据库中的users表，存储用户登录和权限信息
 * 
 * @since v2.0 新增外键关联到借款人表
 */
@Entity
@Table(name = "biz_users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BizUser {
    
    /**
     * 用户ID，主键
     * 自增生成策略
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 用户名，唯一
     * 非空字段，最大长度50
     */
    @Column(nullable = false, length = 50, unique = true)
    private String username;

    /**
     * 密码哈希值
     * 非空字段，最大长度255
     */
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    /**
     * 权限级别
     * 1-普通用户，2-管理员，3-超级管理员
     */
    @Column(name = "permission_level", nullable = false)
    private Integer permissionLevel;

    /**
     * 账户状态
     * 1-正常，0-禁用，2-锁定
     */
    @Column(nullable = false)
    private Integer status;

    /**
     * 注册时间
     * 自动设置为当前时间戳
     */
    @Column(name = "register_time", updatable = false, insertable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime registerTime;

    /**
     * 最后登录时间
     * 可为空
     */
    @Column(name = "last_login_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginTime;

    /**
     * 最后更新时间
     * 数据库自动更新
     */
    @Column(name = "updated_time", insertable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    /**
     * 用户详细信息
     * 一对一关系：一个用户对应一条详细信息
     */
    @OneToOne(mappedBy = "bizUser", cascade = CascadeType.ALL)
    @JsonManagedReference
    private BizUserInfo bizUserInfo;

    /**
     * 关联的借款人列表
     * 一对多关系：一个用户可以关联多个借款人
     * 当用户删除时，关联借款人的user_id设为NULL
     */
    @OneToMany(mappedBy = "bizUser", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Borrowers> borrowers;
}
