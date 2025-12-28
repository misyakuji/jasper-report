package com.misyakuji.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户详细信息实体类
 * 映射到数据库中的user_info表，存储用户详细个人信息
 */
@Entity
@Table(name = "biz_user_info")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BizUserInfo {

    /**
     * 信息ID，主键
     * 自增生成策略
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "info_id")
    private Integer infoId;

    /**
     * 关联的用户ID，外键
     * 唯一约束，确保一个用户只有一条信息记录
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true, 
                foreignKey = @ForeignKey(name = "fk_biz_user_info_user"))
    @JsonBackReference
    private BizUser bizUser;

    /**
     * 真实姓名
     * 非空字段，默认为空字符串
     */
    @Column(name = "real_name", nullable = false, length = 100)
    private String realName;

    /**
     * 手机号码
     * 唯一约束
     */
    @Column(length = 20, unique = true)
    private String phone;

    /**
     * 电子邮箱
     * 唯一约束
     */
    @Column(length = 100, unique = true)
    private String email;

    /**
     * 地址
     */
    @Column()
    private String address;

    /**
     * 出生日期
     */
    @Column(name = "birth_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    /**
     * 性别
     * M-男，F-女，U-未知
     */
    @Column(length = 1)
    private String gender;

    /**
     * 头像URL
     */
    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    /**
     * 座位号/工位号
     */
    @Column(name = "seat_number", length = 20)
    private String seatNumber;

    /**
     * 所属部门/实体
     */
    @Column(name = "department", length = 100)
    private String department;

    /**
     * 直属领导用户ID
     * 外键，引用users表
     */
    @Column(name = "direct_leader_id")
    private Integer directLeaderId;

    /**
     * 职位
     */
    @Column(length = 100)
    private String position;

    /**
     * 入职日期
     */
    @Column(name = "hire_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate hireDate;

    /**
     * 所属组/团队
     */
    @Column(length = 100)
    private String team;

    /**
     * 信息状态
     * 1-正常，0-离职
     */
    @Column(columnDefinition = "TINYINT(1) DEFAULT 1")
    private Integer status;

    /**
     * 创建时间
     * 数据库自动生成
     */
    @Column(name = "created_time", updatable = false, insertable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    /**
     * 最后更新时间
     * 数据库自动更新
     */
    @Column(name = "updated_time", insertable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

}