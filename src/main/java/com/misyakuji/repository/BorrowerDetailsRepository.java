package com.misyakuji.repository;

import com.misyakuji.entity.BorrowerDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * 借款人交易明细数据访问层接口
 * 提供对borrower_details表的CRUD操作、复杂查询和自定义查询功能
 */
public interface BorrowerDetailsRepository extends JpaRepository<BorrowerDetails, Integer>,
        JpaSpecificationExecutor<BorrowerDetails> {

    /**
     * 根据借款人ID查询所有相关的交易明细
     * Spring Data JPA会根据方法名自动生成查询实现
     * @param borrowerId 借款人ID
     * @return 包含该借款人所有交易明细的列表
     */
    List<BorrowerDetails> findByBorrowerId(Integer borrowerId);
}