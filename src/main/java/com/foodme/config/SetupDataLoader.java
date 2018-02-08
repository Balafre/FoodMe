package com.foodme.config;


import com.foodme.model.Account;
import com.foodme.repository.AccountRepository;
import com.foodme.model.AccountAuthority;
import com.foodme.repository.AccountAuthorityRepository;
import com.foodme.enumeration.Authority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private boolean alreadySetup = false;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountAuthorityRepository authorityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    @Transactional
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        if (alreadySetup) {
            return;
        }

        final AccountAuthority adminRole =
                createAuthorityIfNotFound(Authority.ROLE_ADMIN, "Admin Authority");
        createAuthorityIfNotFound(Authority.ROLE_USER, "User Authority");
        createAuthorityIfNotFound(Authority.ROLE_STAFF, "Staff Authority");
        createAuthorityIfNotFound(Authority.ROLE_CUSTOMER, "Customer Authority");

        final Account user = new Account("admin@test.com", passwordEncoder.encode("tesT123"));
        user.setFirstName("Test");
        user.setLastName("Test");
        user.setAuthorities(new HashSet(Arrays.asList(adminRole)));
        user.setEnabled(true);
//        user.setCreatedBy(null);
        accountRepository.save(user);

        alreadySetup = true;
    }

    @Transactional
    private final AccountAuthority createAuthorityIfNotFound(final Authority authority, final String description) {
        AccountAuthority accAuthority = authorityRepository.findOneByAuthority(authority);
        if (accAuthority == null) {
            accAuthority = new AccountAuthority(authority, description);
            accAuthority = authorityRepository.save(accAuthority);
        }
        return accAuthority;
    }

}


