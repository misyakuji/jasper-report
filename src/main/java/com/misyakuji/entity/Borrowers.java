package com.misyakuji.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Borrowers")
@Data
public class Borrowers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "contact_info", length = 255)
    private String contactInfo;

    @Column(name = "loan_start_date")
    private LocalDate loanStartDate;

    @Column(name = "clearance_date")
    private LocalDate clearanceDate;

    @Column(name = "total_loan_amount", precision = 10, scale = 2)
    private BigDecimal totalLoanAmount;

    @Column(name = "total_interest_amount", precision = 10, scale = 2)
    private BigDecimal totalInterestAmount;

    @Column(name = "remaining_balance", precision = 10, scale = 2)
    private BigDecimal remainingBalance;

    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @CreationTimestamp
    @Column(name = "created_time", updatable = false)
    private LocalDateTime createdTime;

    @UpdateTimestamp
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;
}