package com.misyakuji.service;

import com.misyakuji.entity.Borrowers;
import com.misyakuji.repository.BorrowersRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BorrowersService {

    private final BorrowersRepository repository;

    public BorrowersService(BorrowersRepository repository) {
        this.repository = repository;
    }

    public Borrowers create(Borrowers borrower) {
        return repository.save(borrower);
    }

    public Borrowers update(Integer id, Borrowers borrower) {
        return repository.findById(id)
                .map(existing -> {
                    borrower.setId(id);
                    return repository.save(borrower);
                })
                .orElseThrow(() -> new EntityNotFoundException("Borrower not found"));
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }

    public Borrowers getById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Borrower not found"));
    }

    public List<Borrowers> getAll() {
        return repository.findAll();
    }
}