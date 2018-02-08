package com.foodme.dto;

import com.foodme.model.AccountAuthority;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Data
public class AccountDto implements UserDetails {
    private Long id;

    private String firstName;

    private String lastName;

    private String username;

    private String password;

    private Date createdDate;

    private Date updatedDate;

    private boolean accountNonExpired;

    private boolean accountNonLocked;

    private boolean credentialsNonExpired;

    private boolean enabled;

    private Set<AccountAuthority> authorities;


    private AccountDto() {
    }

    public AccountDto(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public AccountDto(String username, String password, AccountAuthority authority) {
        this(username, password);
        if (authority != null) {
            this.authorities.add(authority);
        }
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Set<? extends GrantedAuthority> getAuthorities() {
        return Collections.unmodifiableSet(authorities);
    }
}
