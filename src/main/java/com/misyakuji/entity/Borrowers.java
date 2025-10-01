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

@Entity
@Table(name = "borrowers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Borrowers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "tel")
    private String tel;

    @Column(name = "start_date")
    private String startDate;

    @Column(name = "end_date")
    private String endDate;

    @Column(name = "total_loan", precision = 10, scale = 2)
    private BigDecimal totalLoan;

    @Column(name = "total_interest", precision = 10, scale = 2)
    private BigDecimal totalInterest;

    @Column(name = "remaining_balance", precision = 10, scale = 2)
    private BigDecimal remainingBalance;

    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "created_time", columnDefinition = "TEXT", updatable = false, insertable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    private LocalDateTime createdTime;

    @Column(name = "updated_time", columnDefinition = "TEXT", insertable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    private LocalDateTime updatedTime;

    @OneToMany(mappedBy = "borrower", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<BorrowerDetails> borrowerDetails;
}