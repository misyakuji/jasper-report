package com.misyakuji.aspect;

import com.misyakuji.entity.BorrowerDetails;
import com.misyakuji.repository.BorrowerDetailsRepository;
import com.misyakuji.service.BorrowersService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * BorrowerDetails操作切面
 * 在对BorrowerDetails进行增删改操作后自动执行calculator处理
 */
@Aspect
@Component
@Slf4j
public class BorrowerDetailsAspect {
    
    private final BorrowersService borrowersService;
    private final BorrowerDetailsRepository borrowerDetailsRepository;
    
    public BorrowerDetailsAspect(BorrowersService borrowersService, BorrowerDetailsRepository borrowerDetailsRepository) {
        this.borrowersService = borrowersService;
        this.borrowerDetailsRepository = borrowerDetailsRepository;
    }

    // 切点：拦截BorrowerDetailsService中的增删改方法
    @Pointcut("execution(* com.misyakuji.service.BorrowerDetailsService.create(..)) " +
            "|| execution(* com.misyakuji.service.BorrowerDetailsService.update(..)) " +
            "|| execution(* com.misyakuji.service.BorrowerDetailsService.delete(..)) " +
            "|| execution(* com.misyakuji.service.BorrowerDetailsService.createAll(..)) " +
            "|| execution(* com.misyakuji.service.BorrowerDetailsService.updateAll(..))")
    public void borrowerDetailsModifyOperation() {}
    
    /**
     * 处理单个BorrowerDetails创建后的calculator调用
     */
    @AfterReturning(value = "execution(* com.misyakuji.service.BorrowerDetailsService.create(..))", returning = "result")
    public void afterCreate(BorrowerDetails result) {
        if (result != null && result.getBorrower() != null) {
            Integer borrowerId = result.getBorrower().getId();
            log.debug("创建BorrowerDetails后自动执行calculator，borrowerId: {}", borrowerId);
            borrowersService.calculator(borrowerId);
        }
    }
    
    /**
     * 处理单个BorrowerDetails更新后的calculator调用
     */
    @AfterReturning(value = "execution(* com.misyakuji.service.BorrowerDetailsService.update(..))", returning = "result")
    public void afterUpdate(BorrowerDetails result) {
        if (result != null && result.getBorrower() != null) {
            Integer borrowerId = result.getBorrower().getId();
            log.debug("更新BorrowerDetails后自动执行calculator，borrowerId: {}", borrowerId);
            borrowersService.calculator(borrowerId);
        }
    }
    
    /**
     * 处理单个BorrowerDetails删除的around切面
     * 在执行delete之前获取borrowerId，执行后根据执行状况触发calculator处理
     */
    @Around("execution(* com.misyakuji.service.BorrowerDetailsService.delete(..)) && args(id)")
    public Object aroundDelete(ProceedingJoinPoint joinPoint, Integer id) throws Throwable {
        log.debug("开始处理delete请求，id: {}", id);
        
        // 执行delete前，先获取对应的BorrowerDetails以获取borrowerId
        BorrowerDetails borrowerDetails = borrowerDetailsRepository.findById(id).orElse(null);
        Integer borrowerId = null;
        
        if (borrowerDetails != null && borrowerDetails.getBorrower() != null) {
            borrowerId = borrowerDetails.getBorrower().getId();
            log.debug("获取到borrowerId: {}", borrowerId);
        }
        
        try {
            // 执行原delete方法
            Object result = joinPoint.proceed();
            
            // 如果删除成功且获取到了borrowerId，则触发calculator处理
            if (borrowerId != null) {
                log.debug("删除BorrowerDetails成功后自动执行calculator，borrowerId: {}", borrowerId);
                borrowersService.calculator(borrowerId);
            }
            
            return result;
        } catch (Throwable throwable) {
            // 记录异常信息，但不阻止异常向上传播
            log.error("执行delete操作异常: {}", throwable.getMessage());
            throw throwable;
        }
    }
    
    /**
     * 处理批量创建后的calculator调用
     */
    @AfterReturning(value = "execution(* com.misyakuji.service.BorrowerDetailsService.createAll(..))", returning = "result")
    public void afterCreateAll(List<BorrowerDetails> result) {
        if (result != null && !result.isEmpty() && result.get(0) != null && result.get(0).getBorrower() != null) {
            Integer borrowerId = result.get(0).getBorrower().getId();
            log.debug("批量创建BorrowerDetails后自动执行calculator，borrowerId: {}", borrowerId);
            borrowersService.calculator(borrowerId);
        }
    }
    
    /**
     * 处理批量更新后的calculator调用
     */
    @AfterReturning(value = "execution(* com.misyakuji.service.BorrowerDetailsService.updateAll(..))", returning = "result")
    public void afterUpdateAll(List<BorrowerDetails> result) {
        if (result != null && !result.isEmpty() && result.get(0) != null && result.get(0).getBorrower() != null) {
            Integer borrowerId = result.get(0).getBorrower().getId();
            log.debug("批量更新BorrowerDetails后自动执行calculator，borrowerId: {}", borrowerId);
            borrowersService.calculator(borrowerId);
        }
    }
}