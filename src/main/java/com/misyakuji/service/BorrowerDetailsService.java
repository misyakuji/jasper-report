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
                    borrower.setId(id);
                    return repository.save(borrower);
                })
                .orElseThrow(() -> new EntityNotFoundException("BorrowerDetail not found"));
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }

    public BorrowerDetails getById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Borrower not found"));
    }

    public List<BorrowerDetails> getAll() {
        return repository.findAll();
    }
}