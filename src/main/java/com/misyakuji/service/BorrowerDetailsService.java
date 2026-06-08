package com.misyakuji.service;

import com.misyakuji.entity.BorrowerDetails;
import com.misyakuji.repository.BorrowerDetailsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BorrowerDetailsService {

    private final BorrowerDetailsRepository repository;

    public BorrowerDetailsService(BorrowerDetailsRepository repository) {
        this.repository = repository;
    }

    public BorrowerDetails create(BorrowerDetails borrower) {
        return repository.save(borrower);
    }

    public BorrowerDetails update(Integer id, BorrowerDetails borrower) {
        return repository.findById(id)
                .map(existing -> {
                    if (borrower.getTransactionType() != null) {
                        existing.setTransactionType(borrower.getTransactionType());
                    }
                    if (borrower.getAmount() != null) {
                        existing.setAmount(borrower.getAmount());
                    }
                    if (borrower.getTransactionDate() != null) {
                        existing.setTransactionDate(borrower.getTransactionDate());
                    }
                    if (borrower.getNotes() != null) {
                        existing.setNotes(borrower.getNotes());
                    }
                    if (borrower.getBorrower() != null) {
                        existing.setBorrower(borrower.getBorrower());
                    }
                    return repository.save(existing);
                })
                .orElseThrow(() -> new EntityNotFoundException("BorrowerDetail not found"));
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }

    public List<BorrowerDetails> findByBorrowerId(Integer borrowerId) {
        return repository.findByBorrowerBorrowerId(borrowerId);
    }
    public List<BorrowerDetails> getAll() {
        return repository.findAll();
    }

    public List<BorrowerDetails> createAll(List<BorrowerDetails> borrowers) {
        return repository.saveAll(borrowers);
    }

    public List<BorrowerDetails> updateAll(List<BorrowerDetails> borrowers) {
        return repository.saveAll(borrowers);
    }
}