package com.foodme.model;

import com.foodme.enumeration.Authority;
import com.foodme.enumeration.SocialMediaService;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(indexes = {@Index(name = "idx_account_name",  columnList="username", unique = true)})
public class Account extends ShortAuditEntity {

    @Length(max = 50)
    private String firstName;

    @Length(max = 50)
    private String lastName;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false, length = 100)
    private String password;

    private boolean accountNonExpired = true;

    private boolean accountNonLocked = true;

    private boolean credentialsNonExpired = true;

    private boolean enabled = false;

    private SocialMediaService socialMediaService;

    @ManyToMany(
            fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "account_authority",
            joinColumns = @JoinColumn(name = "account_id", nullable = false, updatable = false),
            inverseJoinColumns = { @JoinColumn(name = "authority_id", nullable = false, updatable = false) })
    private Set<AccountAuthority> authorities = new HashSet<>();

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL)
    private Set<Restaurant> restaurants = new HashSet<>();


    private Account() {
	}

	
	public Account(String username, String password) {
        this.username = username;
        this.password = password;
	}

    public Account(String username, String password, AccountAuthority authority) {
        this(username, password);
        if(authority != null) { this.authorities.add(authority); }
    }

    public void addAuthority(Authority authority) {
        this.authorities.add(new AccountAuthority(authority));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Account acc = (Account) obj;
        if (!username.equals(acc.username)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Account [firstName=").append(firstName).append("]")
                .append("[lastName=").append(lastName).append("]")
                .append("[username").append(username).append("]");
        return builder.toString();
    }
}

