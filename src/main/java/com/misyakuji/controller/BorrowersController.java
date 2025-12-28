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
 * 
 * @since v2.0 新增用户关联相关API接口
 */
@RestController
@RequestMapping(value = "/borrowers", produces = "application/json")
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

    // ==================== v2.0 新增：用户关联相关API ====================

    /**
     * 根据用户ID查询关联的借款人列表
     * @param userId 用户ID
     * @return 关联的借款人列表及HTTP 200状态码
     */
    @GetMapping("/user/{userId}")  // GET /borrowers/bizUser/{userId}
    public ResponseEntity<List<Borrowers>> getByUserId(@PathVariable Integer userId) {
        return ResponseEntity.ok(service.getByUserId(userId));
    }

    /**
     * 查询所有未关联用户的借款人
     * @return 未关联用户的借款人列表及HTTP 200状态码
     */
    @GetMapping("/unlinked")  // GET /borrowers/unlinked
    public ResponseEntity<List<Borrowers>> getUnlinkedBorrowers() {
        return ResponseEntity.ok(service.getUnlinkedBorrowers());
    }

    /**
     * 检查指定用户是否已关联借款人
     * @param userId 用户ID
     * @return 检查结果及HTTP 200状态码
     */
    @GetMapping("/check-linked/{userId}")  // GET /borrowers/check-linked/{userId}
    public ResponseEntity<Boolean> checkUserLinked(@PathVariable Integer userId) {
        return ResponseEntity.ok(service.hasLinkedBorrowers(userId));
    }

    /**
     * 关联借款人到指定用户
     * @param borrowerId 借款人ID
     * @param userId 用户ID
     * @return 更新成功的借款人对象及HTTP 200状态码
     * @throws EntityNotFoundException 当借款人或用户不存在时抛出
     * @throws IllegalArgumentException 当用户已关联其他借款人时抛出
     */
    @PostMapping("/{borrowerId}/link/{userId}")  // POST /borrowers/{borrowerId}/link/{userId}
    public ResponseEntity<Borrowers> linkToUser(@PathVariable Integer borrowerId, @PathVariable Integer userId) {
        return ResponseEntity.ok(service.linkToUser(borrowerId, userId));
    }

    /**
     * 取消借款人与用户的关联
     * @param borrowerId 借款人ID
     * @return 更新成功的借款人对象及HTTP 200状态码
     * @throws EntityNotFoundException 当借款人不存在时抛出
     */
    @PostMapping("/{borrowerId}/unlink")  // POST /borrowers/{borrowerId}/unlink
    public ResponseEntity<Borrowers> unlinkFromUser(@PathVariable Integer borrowerId) {
        return ResponseEntity.ok(service.unlinkFromUser(borrowerId));
    }

    /**
     * 根据用户ID或借款人姓名搜索借款人
     * @param userId 用户ID（可选，查询参数）
     * @param name 借款人姓名（可选，查询参数）
     * @return 匹配的借款人列表及HTTP 200状态码
     */
    @GetMapping("/search")  // GET /borrowers/search?userId=1&name=张
    public ResponseEntity<List<Borrowers>> searchBorrowers(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String name) {
        return ResponseEntity.ok(service.searchBorrowers(userId, name));
    }

    /**
     * 批量关联借款人到用户
     * @param borrowerIds 借款人ID列表
     * @param userId 用户ID
     * @return 关联成功的借款人列表及HTTP 200状态码
     * @throws EntityNotFoundException 当用户不存在时抛出
     */
    @PostMapping("/batch-link/{userId}")  // POST /borrowers/batch-link/{userId}
    public ResponseEntity<List<Borrowers>> batchLinkToUser(@RequestBody List<Integer> borrowerIds, 
                                                       @PathVariable Integer userId) {
        return ResponseEntity.ok(service.batchLinkToUser(borrowerIds, userId));
    }

    /**
     * 获取借款人的完整信息（包含用户信息和交易明细）
     * @param borrowerId 借款人ID
     * @return 包含完整信息的借款人对象及HTTP 200状态码
     * @throws EntityNotFoundException 当借款人不存在时抛出
     */
    @GetMapping("/{borrowerId}/full-details")  // GET /borrowers/{borrowerId}/full-details
    public ResponseEntity<Borrowers> getBorrowerWithFullDetails(@PathVariable Integer borrowerId) {
        return ResponseEntity.ok(service.getBorrowerWithFullDetails(borrowerId));
    }

    /**
     * 根据用户ID获取关联借款人的完整信息
     * @param userId 用户ID
     * @return 包含完整信息的借款人列表及HTTP 200状态码
     */
    @GetMapping("/user/{userId}/full-details")  // GET /borrowers/bizUser/{userId}/full-details
    public ResponseEntity<List<Borrowers>> getBorrowersWithFullDetailsByUserId(@PathVariable Integer userId) {
        return ResponseEntity.ok(service.getBorrowersWithFullDetailsByUserId(userId));
    }
}