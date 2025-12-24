package com.misyakuji.controller;

import com.misyakuji.entity.BorrowerDetails;
import com.misyakuji.service.BorrowerDetailsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/borrowerDetails", produces = "application/json")
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
    public ResponseEntity<List<BorrowerDetails>> findByBorrowerId(@PathVariable Integer id) {
        return ResponseEntity.ok(service.findByBorrowerId(id));
    }

    @GetMapping
    public ResponseEntity<List<BorrowerDetails>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping("/all")
    public ResponseEntity<List<BorrowerDetails>> createAll(@RequestBody List<BorrowerDetails> details) {
        return new ResponseEntity<>(service.createAll(details), HttpStatus.CREATED);
    }

}