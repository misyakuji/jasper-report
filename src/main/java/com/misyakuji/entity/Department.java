package com.misyakuji.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 部门实体类
 * 映射到数据库中的department表，存储部门信息
 */
@Entity
@Table(name = "department")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Department {
    /**
     * 部门ID，主键
     * 自增生成策略
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "department_id")
    private Integer departmentId;

    /**
     * 部门名称
     * 非空字段，最大长度100
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * 部门描述
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 部门经理ID，外键
     * 引用users表的主键
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", 
                foreignKey = @ForeignKey(name = "fk_department_manager"))
    @JsonBackReference
    private BizUser manager;

    /**
     * 创建时间
     * 数据库自动生成
     */
    @Column(name = "created_time", updatable = false, insertable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    /**
     * 更新时间
     * 数据库自动更新
     */
    @Column(name = "updated_time", insertable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

}