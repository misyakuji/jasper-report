package com.misyakuji.repository;

import com.misyakuji.entity.BorrowerDetails;
import com.misyakuji.entity.Borrowers;
import com.misyakuji.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 借款人交易明细数据访问层接口
 * 提供对borrower_details表的CRUD操作、复杂查询和自定义查询功能
 * 
 * @since v2.0 新增基于用户关联的查询方法
 */
public interface BorrowerDetailsRepository extends JpaRepository<BorrowerDetails, Integer>,
        JpaSpecificationExecutor<BorrowerDetails> {

    /**
     * 根据借款人ID查询所有相关的交易明细
     * Spring Data JPA会根据方法名自动生成查询实现
     * @param borrowerId 借款人ID
     * @return 包含该借款人所有交易明细的列表
     */
    List<BorrowerDetails> findByBorrowerBorrowerId(Integer borrowerId);

    /**
     * 根据借款人对象查询所有相关的交易明细
     * @param borrower 借款人对象
     * @return 包含该借款人所有交易明细的列表
     */
    List<BorrowerDetails> findByBorrower(Borrowers borrower);

    /**
     * 根据用户ID查询相关的交易明细
     * @param userId 用户ID
     * @return 该用户关联的借款人的所有交易明细
     */
    @Query("SELECT bd FROM BorrowerDetails bd WHERE bd.borrower.bizUser.userId = :userId")
    List<BorrowerDetails> findByBizUserUserId(@Param("userId") Integer userId);

    /**
     * 根据交易类型查询交易明细
     * @param transactionType 交易类型
     * @return 指定类型的交易明细列表
     */
    List<BorrowerDetails> findByTransactionType(TransactionType transactionType);

    /**
     * 根据借款人ID和交易类型查询交易明细
     * @param borrowerId 借款人ID
     * @param transactionType 交易类型
     * @return 符合条件的交易明细列表
     */
    List<BorrowerDetails> findByBorrowerBorrowerIdAndTransactionType(Integer borrowerId, TransactionType transactionType);

    /**
     * 根据用户ID和交易类型查询交易明细
     * @param userId 用户ID
     * @param transactionType 交易类型
     * @return 符合条件的交易明细列表
     */
    @Query("SELECT bd FROM BorrowerDetails bd WHERE bd.borrower.bizUser.userId = :userId AND bd.transactionType = :transactionType")
    List<BorrowerDetails> findByUserIdAndTransactionType(@Param("userId") Integer userId, @Param("transactionType") TransactionType transactionType);

    /**
     * 根据交易日期范围查询交易明细
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 指定日期范围内的交易明细列表
     */
    //@Query("SELECT bd FROM BorrowerDetails bd WHERE bd.transactionDate BETWEEN :startDate AND :endDate")
    List<BorrowerDetails> findByTransactionDateBetween(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 根据用户ID和交易日期范围查询交易明细
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 符合条件的交易明细列表
     */
    @Query("SELECT bd FROM BorrowerDetails bd WHERE bd.borrower.bizUser.userId = :userId AND bd.transactionDate BETWEEN :startDate AND :endDate")
    List<BorrowerDetails> findByUserIdAndTransactionDateBetween(@Param("userId") Integer userId, 
                                                                 @Param("startDate") String startDate, 
                                                                 @Param("endDate") String endDate);

    /**
     * 计算指定借款人的总金额（按交易类型分组）
     * @param borrowerId 借款人ID
     * @return 按交易类型分组的总金额统计
     */
    @Query("SELECT bd.transactionType, SUM(bd.amount) FROM BorrowerDetails bd WHERE bd.borrower.borrowerId = :borrowerId GROUP BY bd.transactionType")
    List<Object[]> sumAmountByBorrowerIdAndTransactionType(@Param("borrowerId") Integer borrowerId);

    /**
     * 计算指定用户关联的所有借款人的总金额（按交易类型分组）
     * @param userId 用户ID
     * @return 按交易类型分组的总金额统计
     */
    @Query("SELECT bd.transactionType, SUM(bd.amount) FROM BorrowerDetails bd WHERE bd.borrower.bizUser.userId = :userId GROUP BY bd.transactionType")
    List<Object[]> sumAmountByUserIdAndTransactionType(@Param("userId") Integer userId);

    /**
     * 获取最新的交易记录
     * @return 最新的交易记录列表
     */
    @Query("SELECT bd FROM BorrowerDetails bd ORDER BY bd.transactionDate DESC, bd.createdTime DESC")
    List<BorrowerDetails> findLatestTransactions();

    /**
     * 获取指定用户最新的交易记录
     * @param userId 用户ID
     * @return 指定用户最新的交易记录列表
     */
    @Query("SELECT bd FROM BorrowerDetails bd WHERE bd.borrower.bizUser.userId = :userId ORDER BY bd.transactionDate DESC, bd.createdTime DESC")
    List<BorrowerDetails> findLatestTransactionsByUserId(@Param("userId") Integer userId);

}