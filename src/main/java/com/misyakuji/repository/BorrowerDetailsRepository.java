package com.misyakuji.repository;

import com.misyakuji.entity.BorrowerDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface BorrowerDetailsRepository extends JpaRepository<BorrowerDetails, Integer>,
        JpaSpecificationExecutor<BorrowerDetails> {
    List<BorrowerDetails> findByBorrowerId(Integer borrowerId);
}