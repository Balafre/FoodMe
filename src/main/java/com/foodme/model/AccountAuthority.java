package com.foodme.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.foodme.enumeration.Authority;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "authority")
public class AccountAuthority extends BaseEntity implements GrantedAuthority {
    @Column(name = "authority", nullable = false)
    private Authority authority;

    @Column(name = "description", nullable = true, length = 50)
    private String description;

    private AccountAuthority() {
    }

    public AccountAuthority(Authority authority) {
        this.authority = authority;
    }

    public AccountAuthority(Authority authority, String description) {
        this(authority);
        this.description = description;
    }

    @Override
    public String getAuthority() {
        return authority.toString();
    }

    @JsonIgnore
    public Authority getAuthorityEnum() {
        return authority;
    }
}
