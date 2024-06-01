package com.example.assignment.global.config.security.userDetails;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.assignment.domain.user.entity.AmUser;

import lombok.Getter;

@Getter
public class AuthUser extends AmUser implements UserDetails {

    private Long seq;
    private String id;
    private String password;
    private List<String> roles;
    private String nickname;

    private AuthUser(AmUser amUser) {
        this.seq = amUser.getSeq();
        this.id = amUser.getId();
        this.password = amUser.getPassword();
        this.roles = List.of(amUser.getAuthType().toString());
        this.nickname = amUser.getNickname();
    }

    private AuthUser(Long seq, List<String> roles) {
        this.seq = seq;
        this.password = "";
        this.roles = roles;
    }

    public static AuthUser of(AmUser amUser) {
        return new AuthUser(amUser);
    }

    public static AuthUser of(Long id, List<String> roles) {
        return new AuthUser(id, roles);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(roles.get(0)));
    }

    @Override
    public String getUsername() {
        return String.valueOf(seq);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
