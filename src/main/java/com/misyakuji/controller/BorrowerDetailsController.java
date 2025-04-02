package com.misyakuji.controller;

import com.misyakuji.entity.BorrowerDetails;
import com.misyakuji.service.BorrowerDetailsService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/borrowerDetails")
@CrossOrigin(origins = "*")
public class BorrowerDetailsController {

    private final BorrowerDetailsService service;

    public BorrowerDetailsController(BorrowerDetailsService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<BorrowerDetails> create(@RequestBody BorrowerDetails details) {
        return new ResponseEntity<>(service.create(details), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BorrowerDetails> update(@PathVariable Integer id, @RequestBody BorrowerDetails borrower) {
        return ResponseEntity.ok(service.update(id, borrower));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BorrowerDetails> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<BorrowerDetails>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // 统一异常处理
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}