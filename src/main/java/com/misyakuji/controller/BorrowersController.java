package com.misyakuji.controller;

import com.misyakuji.entity.Borrowers;
import com.misyakuji.service.BorrowersService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/borrowers")
@CrossOrigin(origins = "*")
public class BorrowersController {

    private final BorrowersService service;

    public BorrowersController(BorrowersService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Borrowers> create(@RequestBody Borrowers borrower) {
        return new ResponseEntity<>(service.create(borrower), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Borrowers> update(@PathVariable Integer id, @RequestBody Borrowers borrower) {
        return ResponseEntity.ok(service.update(id, borrower));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Borrowers> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<Borrowers>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // 统一异常处理
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}