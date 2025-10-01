package com.misyakuji.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.misyakuji.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 借款人交易明细实体类
 * 映射到数据库中的borrower_details表，存储借款人的每笔交易记录
 */
@Entity
@Table(name = "borrower_details")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BorrowerDetails {
    /**
     * 主键ID
     * 自增生成策略
     */
    @Id  // 标记为主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 设置自增生成策略
    private Integer id;

    /**
     * 关联的借款人对象
     * 多对一关系：多条交易明细对应一个借款人
     * 懒加载模式，外键列为borrower_id
     * 使用@JsonBackReference避免JSON序列化时的循环引用
     */
    @ManyToOne(fetch = FetchType.LAZY)  // 多对一关系配置，懒加载模式
    @JoinColumn(name = "borrower_id", nullable = false)
//    @JsonIgnore // 完全忽略序列化, @JsonIgnore可替代主从表的@JsonManagedReference 和 @JsonBackReference
    @JsonBackReference  // 解决JSON序列化的循环引用问题
    private Borrowers borrower;

//    @Column(name = "borrower_id", nullable = false)
//    private Integer borrowerId;

    /**
     * 交易类型
     * 使用TransactionType枚举表示
     * 非空字段，最大长度20
     */
    @Column(name = "transaction_type", nullable = false, length = 20)
    private TransactionType transactionType;

    /**
     * 交易金额
     * 精度为10位数字，其中小数位2位
     * 非空字段
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    /**
     * 交易日期
     * 格式通常为yyyy-MM-dd
     * 非空字段
     */
    @Column(name = "transaction_date", nullable = false)
    private String transactionDate;

    /**
     * 交易备注
     * 数据库列类型为TEXT
     */
    @Column(columnDefinition = "TEXT")
    private String notes;

    /**
     * 创建时间戳
     * 数据库列类型为TEXT
     * 不可更新，不可插入（由数据库自动生成）
     * JSON序列化时使用指定的日期时间格式
     */
    @Column(name = "created_time", columnDefinition = "TEXT", updatable = false, insertable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    private LocalDateTime createdTime;

    /**
     * 更新时间戳
     * 数据库列类型为TEXT
     * 不可插入（由数据库自动生成或更新）
     * JSON序列化时使用指定的日期时间格式
     */
    @Column(name = "updated_time", columnDefinition = "TEXT", insertable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    private LocalDateTime updatedTime;
}