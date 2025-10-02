package com.misyakuji.controller;

import com.misyakuji.entity.Borrowers;
import com.misyakuji.service.BorrowersService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 借款人信息管理控制器
 * 提供借款人信息的增删改查及自动更新等RESTful API接口
 */
@RestController
@RequestMapping("/borrowers")
@CrossOrigin(origins = "*")
public class BorrowersController {

    private final BorrowersService service;

    /**
     * 构造函数，通过依赖注入获取BorrowersService实例
     * @param service 借款人服务层组件
     */
    public BorrowersController(BorrowersService service) {
        this.service = service;
    }

    /**
     * 查询所有借款人信息列表
     * @return 包含所有借款人的列表及HTTP 200状态码
     */
    @GetMapping  // GET /borrowers
    public ResponseEntity<List<Borrowers>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    /**
     * 根据ID查询借款人信息
     * @param id 借款人ID
     * @return 查询到的借款人对象及HTTP 200状态码
     * @throws EntityNotFoundException 当指定ID的借款人不存在时抛出
     */
    @GetMapping("/{id}")  // GET /borrowers/{id}
    public ResponseEntity<Borrowers> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    /**
     * 创建新的借款人记录
     * @param borrower 包含借款人信息的请求体
     * @return 创建成功的借款人对象及HTTP 201状态码
     */
    @PostMapping  // POST /borrowers
    public ResponseEntity<Borrowers> create(@RequestBody Borrowers borrower) {
        return new ResponseEntity<>(service.create(borrower), HttpStatus.CREATED);
    }

    /**
     * 更新指定ID的借款人信息
     * @param id 借款人ID
     * @param borrower 包含更新后借款人信息的请求体
     * @return 更新成功的借款人对象及HTTP 200状态码
     * @throws EntityNotFoundException 当指定ID的借款人不存在时抛出
     */
    @PutMapping("/{id}")  // PUT /borrowers/{id}
    public ResponseEntity<Borrowers> update(@PathVariable Integer id, @RequestBody Borrowers borrower) {
        return ResponseEntity.ok(service.update(id, borrower));
    }

    /**
     * 更新指定ID的借款人信息
     * @param id 借款人ID
     * @param borrower 包含更新后借款人信息的请求体
     * @return 更新成功的借款人对象及HTTP 200状态码
     * @throws EntityNotFoundException 当指定ID的借款人不存在时抛出
     */
    @PatchMapping("/{id}")  // PUT /borrowers/{id}
    public ResponseEntity<Borrowers> patch(@PathVariable Integer id, @RequestBody Borrowers borrower) {
        return ResponseEntity.ok(service.patch(id, borrower));
    }

    /**
     * 计算所有借款人财务信息
     * 根据关联的交易明细自动计算并更新总借款额、利息、剩余还款额等字段
     * @return 更新成功的借款人对象及HTTP 200状态码
     * @throws EntityNotFoundException 当指定ID的借款人不存在时抛出
     */
    @PostMapping("/calculator")  // POST /borrowers/calculator
    public ResponseEntity<List<Borrowers>> calculatorAll() {
        return ResponseEntity.ok(service.calculatorAll());
    }

    /**
     * 计算指定ID的借款人财务信息
     * 根据关联的交易明细自动计算并更新总借款额、利息、剩余还款额等字段
     * @param id 借款人ID
     * @return 更新成功的借款人对象及HTTP 200状态码
     * @throws EntityNotFoundException 当指定ID的借款人不存在时抛出
     */
    @PostMapping("/calculator/{id}")  // POST /borrowers/calculator/{id}
    public ResponseEntity<Borrowers> calculator(@PathVariable Integer id) {
        return ResponseEntity.ok(service.calculator(id));
    }

    /**
     * 删除指定ID的借款人记录
     * @param id 借款人ID
     * @return HTTP 204无内容状态码，表示删除成功
     */
    @DeleteMapping("/{id}")  // DELETE /borrowers/{id}
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }



}