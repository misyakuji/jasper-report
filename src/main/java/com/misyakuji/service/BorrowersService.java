package com.misyakuji.service;

import com.misyakuji.entity.BizUser;
import com.misyakuji.entity.BorrowerDetails;
import com.misyakuji.entity.Borrowers;
import com.misyakuji.enums.TransactionType;
import com.misyakuji.repository.BizUserRepository;
import com.misyakuji.repository.BorrowersRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 借款人服务层实现类
 * 提供借款人数据的业务逻辑处理，包括增删改查和财务信息自动更新等功能
 * 
 * @since v2.0 新增用户关联功能，支持借款人与系统用户的关联管理
 */
@Service
@Transactional
public class BorrowersService {

    // 注入借款人数据访问层组件
    private final BorrowersRepository repository;
    
    // 注入用户数据访问层组件
    private final BizUserRepository bizUserRepository;

    /**
     * 构造函数，通过依赖注入获取数据访问层组件
     * @param repository 借款人数据访问层组件
     * @param bizUserRepository 用户数据访问层组件
     */
    @Autowired
    public BorrowersService(BorrowersRepository repository, BizUserRepository bizUserRepository) {
        this.repository = repository;
        this.bizUserRepository = bizUserRepository;
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
     * 根据ID查询借款人信息（包含用户关联信息）
     * @param id 借款人ID
     * @return 查询到的借款人实体对象（包含用户信息）
     * @throws EntityNotFoundException 当指定ID的借款人不存在时抛出
     */
    public Borrowers getByIdWithUser(Integer id) {
        // 查找指定ID的借款人，包含用户关联信息
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Borrower not found"));
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
        return repository.findById(id)
                .map(existing -> {
                    if (borrower.getName() != null) {
                        existing.setName(borrower.getName());
                    }
                    if (borrower.getTel() != null) {
                        existing.setTel(borrower.getTel());
                    }
                    if (borrower.getStartDate() != null) {
                        existing.setStartDate(borrower.getStartDate());
                    }
                    if (borrower.getEndDate() != null) {
                        existing.setEndDate(borrower.getEndDate());
                    }
                    if (borrower.getTotalLoan() != null) {
                        existing.setTotalLoan(borrower.getTotalLoan());
                    }
                    if (borrower.getTotalInterest() != null) {
                        existing.setTotalInterest(borrower.getTotalInterest());
                    }
                    if (borrower.getRemainingBalance() != null) {
                        existing.setRemainingBalance(borrower.getRemainingBalance());
                    }
                    if (borrower.getTotalAmount() != null) {
                        existing.setTotalAmount(borrower.getTotalAmount());
                    }
                    if (borrower.getBizUser() != null) {
                        existing.setBizUser(borrower.getBizUser());
                    }
                    return repository.save(existing);
                })
                .orElseThrow(() -> new EntityNotFoundException("Borrower not found"));
    }

    /**
     * 部分更新指定ID的借款人信息
     * 只更新传入对象中不为null的字段，保留数据库中已有但未传入的字段值不变
     *
     * @param id 借款人ID
     * @param borrower 包含需要更新字段的借款人对象
     * @return 更新后的借款人实体对象
     * @throws EntityNotFoundException 当指定ID的借款人不存在时抛出
     */
    public Borrowers patch(Integer id, Borrowers borrower) {
        // 查找指定ID的借款人，如果不存在则抛出异常
        return repository.findById(id)
                .map(existing -> {
                    // 只更新不为null的字段
                    if (borrower.getName() != null) {
                        existing.setName(borrower.getName());
                    }
                    if (borrower.getTel() != null) {
                        existing.setTel(borrower.getTel());
                    }
                    if (borrower.getStartDate() != null) {
                        existing.setStartDate(borrower.getStartDate());
                    }
                    if (borrower.getEndDate() != null) {
                        existing.setEndDate(borrower.getEndDate());
                    }
                    if (borrower.getTotalLoan() != null) {
                        existing.setTotalLoan(borrower.getTotalLoan());
                    }
                    if (borrower.getTotalInterest() != null) {
                        existing.setTotalInterest(borrower.getTotalInterest());
                    }
                    if (borrower.getRemainingBalance() != null) {
                        existing.setRemainingBalance(borrower.getRemainingBalance());
                    }
                    if (borrower.getTotalAmount() != null) {
                        existing.setTotalAmount(borrower.getTotalAmount());
                    }
                    // 保存更新后的借款人信息
                    return repository.save(existing);
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
     * 计算指定ID借款人的财务信息
     * 根据关联的交易明细自计算并更新以下字段：
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
    public Borrowers calculator(Integer id) {
        // 查找指定ID的借款人，如果不存在则抛出异常
        Borrowers borrower = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Borrower not found"));
        
        // 获取该借款人的所有交易明细记录
        List<BorrowerDetails> borrowerDetails = borrower.getBorrowerDetails();
        
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
    
    /**
     * 计算所有借款人的财务信息
     */
    public List<Borrowers> calculatorAll() {
        return getAll().stream()
                .filter(borrower -> borrower.getEndDate() == null) // 过滤条件
                .map(borrower -> calculator(borrower.getBorrowerId()))
                .collect(Collectors.toList());
    }

    // ==================== v2.0 新增：用户关联相关方法 ====================

    /**
     * 根据用户ID查询关联的借款人列表
     * @param userId 用户ID
     * @return 关联的借款人列表
     */
    public List<Borrowers> getByUserId(Integer userId) {
        return repository.findByBizUserUserId(userId);
    }

    /**
     * 根据用户查询关联的借款人列表
     * @param bizUser 用户对象
     * @return 关联的借款人列表
     */
    public List<Borrowers> getByUser(BizUser bizUser) {
        return repository.findByBizUser(bizUser);
    }

    /**
     * 查询所有未关联用户的借款人
     * @return user_id为空的借款人列表
     */
    public List<Borrowers> getUnlinkedBorrowers() {
        return repository.findByBizUserIsNull(Sort.by(Sort.Direction.ASC, "borrowerId"));
    }

    /**
     * 检查指定用户是否已关联借款人
     * @param userId 用户ID
     * @return 存在关联记录的数量
     */
    public boolean hasLinkedBorrowers(Integer userId) {
        return repository.countByBizUserUserId(userId) > 0;
    }

    /**
     * 关联借款人到指定用户
     * @param borrowerId 借款人ID
     * @param userId 用户ID
     * @return 更新后的借款人对象
     * @throws EntityNotFoundException 当借款人或用户不存在时抛出
     * @throws IllegalArgumentException 当用户已关联其他借款人时抛出
     */
    public Borrowers linkToUser(Integer borrowerId, Integer userId) {
        // 验证借款人存在
        Borrowers borrower = repository.findById(borrowerId)
                .orElseThrow(() -> new EntityNotFoundException("Borrower not found with id: " + borrowerId));
        
        // 验证用户存在
        BizUser bizUser = bizUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("BizUser not found with id: " + userId));
        
        // 检查用户是否已关联其他借款人（如果业务需要限制一个用户只能关联一个借款人）
        long existingCount = repository.countByBizUserUserId(userId);
        if (existingCount > 0) {
            // 根据业务需求，这里可以选择抛出异常或允许一个用户关联多个借款人
            throw new IllegalArgumentException("BizUser already has linked borrowers");
        }
        
        // 建立关联关系
        borrower.setBizUser(bizUser);
        
        // 保存更新
        return repository.save(borrower);
    }

    /**
     * 取消借款人与用户的关联
     * @param borrowerId 借款人ID
     * @return 更新后的借款人对象
     * @throws EntityNotFoundException 当借款人不存在时抛出
     */
    public Borrowers unlinkFromUser(Integer borrowerId) {
        // 验证借款人存在
        Borrowers borrower = repository.findById(borrowerId)
                .orElseThrow(() -> new EntityNotFoundException("Borrower not found with id: " + borrowerId));
        
        // 取消关联关系
        borrower.setBizUser(null);
        
        // 保存更新
        return repository.save(borrower);
    }

    /**
     * 根据用户ID或借款人姓名搜索借款人
     * @param userId 用户ID（可为空）
     * @param name 借款人姓名（可为空）
     * @return 匹配的借款人列表
     */
    public List<Borrowers> searchBorrowers(Integer userId, String name) {
        if (userId != null && name != null && !name.trim().isEmpty()) {
            return repository.findByUserIdOrNameContaining(userId, name.trim());
        } else if (userId != null) {
            return repository.findByBizUserUserId(userId);
        } else if (name != null && !name.trim().isEmpty()) {
            return repository.findByUserIdOrNameContaining(null, name.trim());
        } else {
            return getAll();
        }
    }

    /**
     * 批量关联借款人到用户
     * @param borrowerIds 借款人ID列表
     * @param userId 用户ID
     * @return 关联成功的借款人列表
     * @throws EntityNotFoundException 当用户不存在时抛出
     */
    @Transactional
    public List<Borrowers> batchLinkToUser(List<Integer> borrowerIds, Integer userId) {
        // 验证用户存在
        BizUser bizUser = bizUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("BizUser not found with id: " + userId));
        
        // 批量获取所有借款人
        List<Borrowers> borrowers = repository.findAllById(borrowerIds);
        
        // 批量更新关联
        borrowers.forEach(borrower -> borrower.setBizUser(bizUser));
        
        // 批量保存
        return repository.saveAll(borrowers);
    }

    /**
     * 获取借款人的完整信息（包含用户信息和交易明细）
     * @param borrowerId 借款人ID
     * @return 包含完整信息的借款人对象
     * @throws EntityNotFoundException 当借款人不存在时抛出
     */
    public Borrowers getBorrowerWithFullDetails(Integer borrowerId) {
        List<Borrowers> borrowers = repository.findAllWithUserAndDetails();
        return borrowers.stream()
                .filter(b -> b.getBorrowerId().equals(borrowerId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Borrower not found with id: " + borrowerId));
    }

    /**
     * 根据用户ID获取关联借款人的完整信息
     * @param userId 用户ID
     * @return 包含完整信息的借款人列表
     */
    public List<Borrowers> getBorrowersWithFullDetailsByUserId(Integer userId) {
        return repository.findByUserIdWithDetails(userId);
    }

    // ==================== 兼容性方法 - 保持向后兼容 ====================

    /**
     * 向后兼容：根据用户ID查询关联的借款人列表（Long类型）
     * @param userId 用户ID（Long类型）
     * @return 关联的借款人列表
     */
    @Deprecated
    public List<Borrowers> getByUserId(Long userId) {
        return getByUserId(userId != null ? userId.intValue() : null);
    }

    /**
     * 向后兼容：关联借款人到指定用户（Long类型）
     * @param borrowerId 借款人ID
     * @param userId 用户ID（Long类型）
     * @return 更新后的借款人对象
     */
    @Deprecated
    public Borrowers linkToUser(Integer borrowerId, Long userId) {
        return linkToUser(borrowerId, userId != null ? userId.intValue() : null);
    }
}