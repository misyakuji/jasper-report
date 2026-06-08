package com.misyakuji.repository;

import com.misyakuji.entity.BizUser;
import com.misyakuji.entity.Borrowers;
import lombok.NonNull;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 借款人数据访问层接口
 * 提供对borrowers表的CRUD操作和复杂查询功能
 * 
 * @since v2.0 新增用户关联查询方法
 */
public interface BorrowersRepository extends JpaRepository<Borrowers, Integer>,
        JpaSpecificationExecutor<Borrowers> {

    /**
     * 根据用户ID查询关联的借款人列表
     * @param userId 用户ID
     * @return 关联的借款人列表
     */
    List<Borrowers> findByBizUserUserId(Integer userId);

    /**
     * 根据用户查询关联的借款人列表
     * @param bizUser 用户对象
     * @return 关联的借款人列表
     */
    List<Borrowers> findByBizUser(BizUser bizUser);

    /**
     * 查询所有未关联用户的借款人
     * @return user_id为空的借款人列表
     */
    List<Borrowers> findByBizUserIsNull(Sort sort);

    /**
     * 检查指定用户是否已关联借款人
     * @param userId 用户ID
     * @return 存在关联记录的数量
     */
    long countByBizUserUserId(Integer userId);

    /**
     * 根据借款人姓名和用户ID查询
     * @param name 借款人姓名
     * @param userId 用户ID（可为空）
     * @return 匹配的借款人列表
     */
    @Query("SELECT b FROM Borrowers b WHERE b.name = :name AND (:userId IS NULL OR b.bizUser.userId = :userId)")
    List<Borrowers> findByNameAndUserId(@Param("name") String name, @Param("userId") Integer userId);

    /**
     * 根据用户ID或借款人姓名搜索借款人
     * @param userId 用户ID（可为空）
     * @param name 借款人姓名（可为空）
     * @return 匹配的借款人列表
     */
    @Query("SELECT b FROM Borrowers b WHERE " +
           "(:userId IS NULL OR b.bizUser.userId = :userId) AND " +
           "(:name IS NULL OR b.name LIKE %:name%)")
    List<Borrowers> findByUserIdOrNameContaining(@Param("userId") Integer userId, @Param("name") String name);

    /**
     * 获取借款人的统计信息（包含用户信息）
     * @return 包含用户信息的借款人汇总
     */
    @Query("SELECT b FROM Borrowers b LEFT JOIN FETCH b.bizUser LEFT JOIN FETCH b.borrowerDetails")
    List<Borrowers> findAllWithUserAndDetails();

    /**
     * 根据借款人ID获取借款人及其详细信息
     * @param borrowerId 借款人ID
     * @return 包含详细信息的借款人列表
     */
    @Query("SELECT b FROM Borrowers b LEFT JOIN FETCH b.bizUser LEFT JOIN FETCH b.borrowerDetails WHERE b.borrowerId = :borrowerId")
    Borrowers findByIdWithUserAndDetails(@Param("borrowerId") Integer borrowerId);

    /**
     * 根据用户ID获取借款人及其详细信息
     * @param userId 用户ID
     * @return 匹配的借款人列表
     */
    @Query("SELECT b FROM Borrowers b LEFT JOIN FETCH b.bizUser LEFT JOIN FETCH b.borrowerDetails WHERE b.bizUser.userId = :userId")
    List<Borrowers> findByUserIdWithDetails(@Param("userId") Integer userId);

    /**
     * 根据借款人姓名和手机号查询借款人
     * @param name 借款人姓名
     * @param tel 借款人手机号
     * @return 匹配的借款人列表
     */
    List<Borrowers> findByNameContainingAndTelContaining(String name, String tel);
    /**
     * 分页查询所有借款人
     * @param pageable 分页信息
     * @return 分页的借款人列表
     */
    @NullMarked
    Page<Borrowers> findAll(Pageable pageable);

    /**
     * 根据用户ID分页查询借款人
     * @param userId 用户ID
     * @param pageable 分页信息
     * @return 分页的借款人列表
     */
    Page<Borrowers> findByBizUserUserId(Integer userId, Pageable pageable);

    /**
     * 查询所有未还的借款人
     * @return 未还的借款人列表
     */
    List<Borrowers> findByEndDateIsNull();
    
    /**
     * 查询所有已还的借款人
     * @return 已还的借款人列表
     */
    List<Borrowers> findByTotalLoanGreaterThanEqual(BigDecimal amount);

     /**
     * 查询所有已还的借款人
     * @return 已还的借款人列表
     */
    List<Borrowers> findByEndDateIsNotNull();

}