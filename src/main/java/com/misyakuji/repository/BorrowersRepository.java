package com.misyakuji.repository;

import com.misyakuji.entity.Borrowers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 借款人数据访问层接口
 * 提供对borrowers表的CRUD操作和复杂查询功能
 */
public interface BorrowersRepository extends JpaRepository<Borrowers, Integer>,
        JpaSpecificationExecutor<Borrowers> {
}