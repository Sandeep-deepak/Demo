package com.supreme.serviceImpl;

import java.util.*;
import java.util.stream.Collectors;

import com.supreme.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class UserDetailsImpl implements UserDetails {

    private static final long serialVersionUID = 1L;
    private Long id;
    private String mobileNumber;
    @JsonIgnore
    private String pin;
    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Long id, String mobileNumber, String pin,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.mobileNumber = mobileNumber;
        this.pin = pin;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = Arrays.stream(user.getRole().name().split(","))
                .map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getId(),
                user.getMobileNumber(),
                user.getPin(),
                authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String getPassword() {
        return pin;
    }

    @Override
    public String getUsername() {
        return mobileNumber;
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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }
}
