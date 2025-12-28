package com.misyakuji.service;

import com.misyakuji.entity.BizUser;
import com.misyakuji.enums.RoleType;
import com.misyakuji.repository.BizUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final BizUserRepository bizUserRepository;

    public MyUserDetailsService(BizUserRepository bizUserRepository) {
        this.bizUserRepository = bizUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        BizUser bizUser = bizUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在"));

        return org.springframework.security.core.userdetails.User.builder()
                .username(bizUser.getUsername())
                .password(bizUser.getPasswordHash())
                .roles(RoleType.getRole(bizUser.getPermissionLevel()))
                .build();
    }
}