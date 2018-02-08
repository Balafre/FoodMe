package com.foodme.repository;

import com.foodme.model.AccountAuthority;
import com.foodme.enumeration.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Lev on 5/16/2016.
 */
@Repository
public interface AccountAuthorityRepository extends JpaRepository<AccountAuthority, Long> {
    AccountAuthority findOneByAuthority(Authority authority);
}
