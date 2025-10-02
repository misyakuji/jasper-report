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

/**
 * 借款人服务层实现类
 * 提供借款人数据的业务逻辑处理，包括增删改查和财务信息自动更新等功能
 */
@Service
public class BorrowersService {

    // 注入借款人数据访问层组件
    private final BorrowersRepository repository;
    // 注入借款人明细数据访问层组件
    private final BorrowerDetailsRepository borrowerDetailsRepository;

    /**
     * 构造函数，通过依赖注入获取数据访问层组件
     * @param repository 借款人数据访问层组件
     * @param borrowerDetailsRepository 借款人明细数据访问层组件
     */
    public BorrowersService(BorrowersRepository repository, BorrowerDetailsRepository borrowerDetailsRepository) {
        this.repository = repository;
        this.borrowerDetailsRepository = borrowerDetailsRepository;
    }

    /**
     * 创建新的借款人记录
     * @param borrower 借款人实体对象
     * @return 保存后的借款人实体对象
     */
    public Borrowers create(Borrowers borrower) {
        // 调用数据访问层保存借款人信息
        return repository.save(borrower);
    }

    /**
     * 更新指定ID的借款人信息
     * @param id 借款人ID
     * @param borrower 包含更新后信息的借款人对象
     * @return 更新后的借款人实体对象
     * @throws EntityNotFoundException 当指定ID的借款人不存在时抛出
     */
    public Borrowers update(Integer id, Borrowers borrower) {
        // 查找指定ID的借款人，如果存在则更新，否则抛出异常
        return repository.findById(id)
                .map(existing -> {
                    // 设置ID以确保更新操作而非创建新记录
                    borrower.setId(id);
                    // 保存更新后的借款人信息
                    return repository.save(borrower);
                })
                .orElseThrow(() -> new EntityNotFoundException("Borrower not found"));
    }

    /**
     * 删除指定ID的借款人记录
     * @param id 借款人ID
     */
    public void delete(Integer id) {
        // 调用数据访问层删除指定ID的借款人记录
        repository.deleteById(id);
    }

    /**
     * 根据ID查询借款人信息
     * @param id 借款人ID
     * @return 查询到的借款人实体对象
     * @throws EntityNotFoundException 当指定ID的借款人不存在时抛出
     */
    public Borrowers getById(Integer id) {
        // 查找指定ID的借款人，如果不存在则抛出异常
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Borrower not found"));
    }

    /**
     * 查询所有借款人信息
     * @return 包含所有借款人的列表
     */
    public List<Borrowers> getAll() {
        // 调用数据访问层获取所有借款人记录
        return repository.findAll();
    }

    /**
     * 自动更新指定ID借款人的财务信息
     * 根据关联的交易明细自动计算并更新以下字段：
     * 1. 总借款额（所有贷款交易的总和）
     * 2. 总利息额（所有利息交易的总和）
     * 3. 剩余还款额（总借款+总利息+总还款）
     * 4. 总金额（总借款+总利息）
     * 5. 当清账日期为空且借款已还清时，自动设置清账日期为当前日期
     * 
     * @param id 借款人ID
     * @return 更新后的借款人实体对象
     * @throws EntityNotFoundException 当指定ID的借款人不存在时抛出
     */
    public Borrowers autoUpdate(Integer id) {
        // 查找指定ID的借款人，如果不存在则抛出异常
        Borrowers borrower = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Borrower not found"));
        
        // 获取该借款人的所有交易明细记录
        List<BorrowerDetails> borrowerDetails = borrowerDetailsRepository.findByBorrowerId(id);
        
        // 使用Stream API统计该借款人的总借款金额
        // 过滤出交易类型为LOAN的记录，并累加其金额
        BigDecimal totalLoan = borrowerDetails.stream()
                .filter(detail -> TransactionType.LOAN.equals(detail.getTransactionType()))
                .map(BorrowerDetails::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 使用Stream API统计该借款人的总利息金额
        // 过滤出交易类型为INTEREST的记录，并累加其金额
        BigDecimal totalInterest = borrowerDetails.stream()
                .filter(detail -> TransactionType.INTEREST.equals(detail.getTransactionType()))
                .map(BorrowerDetails::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 使用Stream API统计该借款人的总还款金额
        // 过滤出交易类型为REPAYMENT的记录，并累加其金额
        // 注意：还款金额在数据模型中可能以负数形式存储
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
        
        // 检查是否满足自动清账条件：
        // 1. 清账日期为空（尚未清账）
        // 2. 剩余还款额小于等于零（已还清或多还）
        if (borrower.getEndDate() == null && remainingBalance.compareTo(BigDecimal.ZERO) <= 0) {
            // 设置日期格式化器，使用"yyyy-MM-dd"格式
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            // 将清账日期设置为当前日期的格式化字符串
            borrower.setEndDate(LocalDate.now().format(formatter));
        }
        
        // 保存更新后的借款人信息
        return repository.save(borrower);
    }
}