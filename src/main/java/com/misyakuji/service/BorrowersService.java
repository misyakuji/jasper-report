package com.misyakuji.service;

import com.misyakuji.entity.Borrowers;
import com.misyakuji.entity.BorrowerDetails;
import com.misyakuji.enums.TransactionType;
import com.misyakuji.repository.BorrowersRepository;
import com.misyakuji.repository.BorrowerDetailsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class BorrowersService {

    private final BorrowersRepository repository;
    private final BorrowerDetailsRepository borrowerDetailsRepository;

    public BorrowersService(BorrowersRepository repository, BorrowerDetailsRepository borrowerDetailsRepository) {
        this.repository = repository;
        this.borrowerDetailsRepository = borrowerDetailsRepository;
    }

    public Borrowers create(Borrowers borrower) {
        return repository.save(borrower);
    }

    public Borrowers update(Integer id, Borrowers borrower) {
        return repository.findById(id)
                .map(existing -> {
                    borrower.setId(id);
                    return repository.save(borrower);
                })
                .orElseThrow(() -> new EntityNotFoundException("Borrower not found"));
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }

    public Borrowers getById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Borrower not found"));
    }

    public List<Borrowers> getAll() {
        return repository.findAll();
    }

    /**
     * 根据borrower_details表数据，更新指定id的借款人信息
     * 1. 统计总借款额，总利息额，剩余还款额，总金额等字段
     * 2. 当清账日期为空时，且当该用户的借款已经还清，则更新清账日期
     */
    public Borrowers autoUpdate(Integer id) {
        // 查找借款人
        Borrowers borrower = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Borrower not found"));
        
        // 获取该借款人的所有交易记录（使用更高效的findByBorrowerId方法）
        List<BorrowerDetails> borrowerDetails = borrowerDetailsRepository.findByBorrowerId(id);
        
        // 统计总借款额
        BigDecimal totalLoan = borrowerDetails.stream()
                .filter(detail -> TransactionType.LOAN.equals(detail.getTransactionType()))
                .map(BorrowerDetails::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 统计总利息额
        BigDecimal totalInterest = borrowerDetails.stream()
                .filter(detail -> TransactionType.INTEREST.equals(detail.getTransactionType()))
                .map(BorrowerDetails::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 统计总还款额
        BigDecimal totalRepayment = borrowerDetails.stream()
                .filter(detail -> TransactionType.REPAYMENT.equals(detail.getTransactionType()))
                .map(BorrowerDetails::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 计算剩余还款额（总借款 + 总利息 + 总还款）
        BigDecimal remainingBalance = totalLoan.add(totalInterest).add(totalRepayment);
        
        // 计算总金额（总借款 + 总利息）
        BigDecimal totalAmount = totalLoan.add(totalInterest);
        
        // 更新借款人信息
        borrower.setTotalLoan(totalLoan);
        borrower.setTotalInterest(totalInterest);
        borrower.setRemainingBalance(remainingBalance);
        borrower.setTotalAmount(totalAmount);
        
        // 检查是否已还清且清账日期为空
        if (borrower.getEndDate() == null && remainingBalance.compareTo(BigDecimal.ZERO) <= 0) {
            // 设置清账日期为当前日期
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            borrower.setEndDate(LocalDate.now().format(formatter));
        }
        
        // 保存更新后的借款人信息
        return repository.save(borrower);
    }
}