package com.misyakuji.service;

import com.misyakuji.entity.BizUser;
import com.misyakuji.entity.BizUserInfo;
import com.misyakuji.repository.BizUserInfoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 用户详细信息服务实现类
 * 提供用户详细信息的业务逻辑处理
 *
 * @since v2.0 完全重构以支持JPA和新的实体结构
 */
@Service
@Transactional
public class BizUserInfoService {

    private final BizUserInfoRepository bizUserInfoRepository;

    /**
     * 构造函数，通过依赖注入获取组件
     * @param bizUserInfoRepository 用户详细信息数据访问层
     */
    @Autowired
    public BizUserInfoService(BizUserInfoRepository bizUserInfoRepository) {
        this.bizUserInfoRepository = bizUserInfoRepository;
    }

    
    public BizUserInfo getByUserId(Integer userId) throws EntityNotFoundException {
        return bizUserInfoRepository.findByBizUserUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("BizUserInfo not found for userId: " + userId));
    }

    
    public Optional<BizUserInfo> getByUser(BizUser bizUser) {
        return bizUserInfoRepository.findByBizUser(bizUser);
    }

    
    public BizUserInfo create(BizUserInfo bizUserInfo) {
        return bizUserInfoRepository.save(bizUserInfo);
    }

    
    public BizUserInfo update(Integer userId, BizUserInfo bizUserInfo) throws EntityNotFoundException {
        BizUserInfo existingInfo = getByUserId(userId);

        // 更新字段
        if (bizUserInfo.getRealName() != null) {
            existingInfo.setRealName(bizUserInfo.getRealName());
        }
        if (bizUserInfo.getPhone() != null) {
            existingInfo.setPhone(bizUserInfo.getPhone());
        }
        if (bizUserInfo.getEmail() != null) {
            existingInfo.setEmail(bizUserInfo.getEmail());
        }
        if (bizUserInfo.getAddress() != null) {
            existingInfo.setAddress(bizUserInfo.getAddress());
        }
        if (bizUserInfo.getBirthDate() != null) {
            existingInfo.setBirthDate(bizUserInfo.getBirthDate());
        }
        if (bizUserInfo.getGender() != null) {
            existingInfo.setGender(bizUserInfo.getGender());
        }
        if (bizUserInfo.getAvatarUrl() != null) {
            existingInfo.setAvatarUrl(bizUserInfo.getAvatarUrl());
        }
        if (bizUserInfo.getSeatNumber() != null) {
            existingInfo.setSeatNumber(bizUserInfo.getSeatNumber());
        }
        if (bizUserInfo.getDepartment() != null) {
            existingInfo.setDepartment(bizUserInfo.getDepartment());
        }
        if (bizUserInfo.getDirectLeaderId() != null) {
            existingInfo.setDirectLeaderId(bizUserInfo.getDirectLeaderId());
        }
        if (bizUserInfo.getPosition() != null) {
            existingInfo.setPosition(bizUserInfo.getPosition());
        }
        if (bizUserInfo.getHireDate() != null) {
            existingInfo.setHireDate(bizUserInfo.getHireDate());
        }
        if (bizUserInfo.getTeam() != null) {
            existingInfo.setTeam(bizUserInfo.getTeam());
        }
        if (bizUserInfo.getStatus() != null) {
            existingInfo.setStatus(bizUserInfo.getStatus());
        }

        return bizUserInfoRepository.save(existingInfo);
    }

    
    public BizUserInfo patch(Integer userId, BizUserInfo bizUserInfo) throws EntityNotFoundException {
        BizUserInfo existingInfo = getByUserId(userId);

        // 只更新非空字段
        if (bizUserInfo.getRealName() != null && !bizUserInfo.getRealName().isEmpty()) {
            existingInfo.setRealName(bizUserInfo.getRealName());
        }
        if (bizUserInfo.getPhone() != null && !bizUserInfo.getPhone().isEmpty()) {
            existingInfo.setPhone(bizUserInfo.getPhone());
        }
        if (bizUserInfo.getEmail() != null && !bizUserInfo.getEmail().isEmpty()) {
            existingInfo.setEmail(bizUserInfo.getEmail());
        }
        if (bizUserInfo.getAddress() != null && !bizUserInfo.getAddress().isEmpty()) {
            existingInfo.setAddress(bizUserInfo.getAddress());
        }
        if (bizUserInfo.getBirthDate() != null) {
            existingInfo.setBirthDate(bizUserInfo.getBirthDate());
        }
        if (bizUserInfo.getGender() != null && !bizUserInfo.getGender().isEmpty()) {
            existingInfo.setGender(bizUserInfo.getGender());
        }
        if (bizUserInfo.getAvatarUrl() != null && !bizUserInfo.getAvatarUrl().isEmpty()) {
            existingInfo.setAvatarUrl(bizUserInfo.getAvatarUrl());
        }
        if (bizUserInfo.getSeatNumber() != null && !bizUserInfo.getSeatNumber().isEmpty()) {
            existingInfo.setSeatNumber(bizUserInfo.getSeatNumber());
        }
        if (bizUserInfo.getDepartment() != null && !bizUserInfo.getDepartment().isEmpty()) {
            existingInfo.setDepartment(bizUserInfo.getDepartment());
        }
        if (bizUserInfo.getDirectLeaderId() != null) {
            existingInfo.setDirectLeaderId(bizUserInfo.getDirectLeaderId());
        }
        if (bizUserInfo.getPosition() != null && !bizUserInfo.getPosition().isEmpty()) {
            existingInfo.setPosition(bizUserInfo.getPosition());
        }
        if (bizUserInfo.getHireDate() != null) {
            existingInfo.setHireDate(bizUserInfo.getHireDate());
        }
        if (bizUserInfo.getTeam() != null && !bizUserInfo.getTeam().isEmpty()) {
            existingInfo.setTeam(bizUserInfo.getTeam());
        }
        if (bizUserInfo.getStatus() != null) {
            existingInfo.setStatus(bizUserInfo.getStatus());
        }

        return bizUserInfoRepository.save(existingInfo);
    }

    
    public void deleteByUserId(Integer userId) throws EntityNotFoundException {
        BizUserInfo bizUserInfo = getByUserId(userId);
        bizUserInfoRepository.delete(bizUserInfo);
    }

    
    public List<BizUserInfo> getAll() {
        return bizUserInfoRepository.findAll();
    }

    
    public List<BizUserInfo> getAllWithUser() {
        return bizUserInfoRepository.findAllWithBizUser();
    }

    
    public Optional<BizUserInfo> getByPhone(String phone) {
        return bizUserInfoRepository.findByPhone(phone);
    }

    
    public Optional<BizUserInfo> getByEmail(String email) {
        return bizUserInfoRepository.findByEmail(email);
    }

    
    public List<BizUserInfo> getByDepartment(String department) {
        return bizUserInfoRepository.findByDepartment(department);
    }

    
    public List<BizUserInfo> getByPosition(String position) {
        return bizUserInfoRepository.findByPosition(position);
    }

    
    public List<BizUserInfo> getByTeam(String team) {
        return bizUserInfoRepository.findByTeam(team);
    }

    
    public List<BizUserInfo> getByStatus(Integer status) {
        return bizUserInfoRepository.findByStatus(status);
    }

    
    public List<BizUserInfo> getByDirectLeaderId(Integer directLeaderId) {
        return bizUserInfoRepository.findByDirectLeaderId(directLeaderId);
    }

    
    public List<BizUserInfo> getByRealNameContaining(String realName) {
        return bizUserInfoRepository.findByRealNameContaining(realName);
    }

    
    public List<BizUserInfo> searchByMultipleConditions(String department, String position, Integer status) {
        return bizUserInfoRepository.findByMultipleConditions(
                department != null && !department.trim().isEmpty() ? department.trim() : null,
                position != null && !position.trim().isEmpty() ? position.trim() : null,
                status
        );
    }

    
    public List<Object[]> countUsersByDepartment() {
        return bizUserInfoRepository.countUsersByDepartment();
    }

    
    public List<Object[]> countUsersByPosition() {
        return bizUserInfoRepository.countUsersByPosition();
    }

    
    public List<Object[]> countSubordinatesByPosition(Integer directLeaderId) {
        return bizUserInfoRepository.countSubordinatesByPosition(directLeaderId);
    }
}