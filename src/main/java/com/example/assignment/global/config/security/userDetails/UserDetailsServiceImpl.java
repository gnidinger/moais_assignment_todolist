package com.example.assignment.global.config.security.userDetails;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.assignment.domain.user.entity.AmUser;
import com.example.assignment.domain.user.repository.AmUserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AmUserRepository amUserRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        return amUserRepository.findById(id)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("유저 정보가 없습니다."));
    }

    private UserDetails createUserDetails(AmUser amUser) {
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(amUser.getAuthType().toString());

        return AuthUser.of(amUser);
    }
}
