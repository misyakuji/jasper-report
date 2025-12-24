package com.misyakuji.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 借款人实体类
 * 映射到数据库中的borrowers表，存储借款人的基本信息和财务概览
 */
@Entity
@Table(name = "borrowers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Borrowers {
    /**
     * 主键ID
     * 自增生成策略
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 设置自增生成策略
    private Integer id;

    /**
     * 借款人姓名
     * 非空字段，最大长度100
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * 借款人联系电话
     * 映射到数据库中的tel列
     */
    @Column(name = "tel")
    private String tel;

    /**
     * 借款开始日期
     * 格式通常为yyyy-MM-dd
     * 映射到数据库中的start_date列
     */
    @Column(name = "start_date")
    private String startDate;

    /**
     * 借款结束/清账日期
     * 格式通常为yyyy-MM-dd
     * 映射到数据库中的end_date列
     */
    @Column(name = "end_date")
    private String endDate;

    /**
     * 总借款金额
     * 精度为10位数字，其中小数位2位
     * 映射到数据库中的total_loan列
     */
    @Column(name = "total_loan", precision = 10, scale = 2)
    private BigDecimal totalLoan;

    /**
     * 总利息金额
     * 精度为10位数字，其中小数位2位
     * 映射到数据库中的total_interest列
     */
    @Column(name = "total_interest", precision = 10, scale = 2)
    private BigDecimal totalInterest;

    /**
     * 剩余还款金额
     * 精度为10位数字，其中小数位2位
     * 映射到数据库中的remaining_balance列
     */
    @Column(name = "remaining_balance", precision = 10, scale = 2)
    private BigDecimal remainingBalance;

    /**
     * 总金额（本金+利息）
     * 精度为10位数字，其中小数位2位
     * 映射到数据库中的total_amount列
     */
    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

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
    @Column(name = "updated_time", insertable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    private LocalDateTime updatedTime;

    /**
     * 借款人的交易明细列表
     * 一对多关系：一个借款人对应多条交易明细
     * 由BorrowerDetails实体中的borrower字段维护关系
     * 级联所有操作，懒加载模式
     * 使用@JsonManagedReference避免JSON序列化时的循环引用
     */
    @OneToMany(mappedBy = "borrower", cascade = CascadeType.ALL, fetch = FetchType.LAZY)  // 一对多关系配置
    @JsonManagedReference  // 解决JSON序列化的循环引用问题
    private List<BorrowerDetails> borrowerDetails;
}