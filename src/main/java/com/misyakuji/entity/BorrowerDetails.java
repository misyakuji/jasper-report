package com.misyakuji.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.misyakuji.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
    @Column(name = "detail_id")
    private Integer detailId;

    /**
     * 关联的借款人对象
     * 多对一关系：多条交易明细对应一个借款人
     * 懒加载模式，外键列为borrower_id
     * 使用@JsonBackReference避免JSON序列化时的循环引用
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrower_id", nullable = false)
    @JsonBackReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
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
     * MariaDB自动生成，不可更新
     * JSON序列化时使用指定的日期时间格式
     */
    @Column(name = "created_time", updatable = false, insertable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    private LocalDateTime createdTime;

    /**
     * 更新时间戳
     * MariaDB自动更新，不可插入
     * JSON序列化时使用指定的日期时间格式
     */
    @Column(name = "updated_time", insertable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    private LocalDateTime updatedTime;

}