package com.misyakuji.repository;

import com.misyakuji.entity.Borrowers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BorrowersRepository extends JpaRepository<Borrowers, Integer>,
        JpaSpecificationExecutor<Borrowers> {
}