package com.misyakuji.repository;

import com.misyakuji.entity.BorrowerDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BorrowerDetailsRepository extends JpaRepository<BorrowerDetails, Integer>,
        JpaSpecificationExecutor<BorrowerDetails> {
}