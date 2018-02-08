package com.foodme.service;

import com.foodme.dto.AccountDto;
import com.foodme.model.Account;
import com.foodme.model.AccountAuthority;
import com.foodme.repository.AccountAuthorityRepository;
import com.foodme.repository.AccountRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

import static com.foodme.util.StreamUtils.streamOf;

@SuppressWarnings("SpringAutowiredFieldsWarningInspection")
@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class AccountService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountAuthorityRepository authorityRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public Account save(Account account) {
        if (account.getUsername() == null || account.getPassword() == null) {
            return null;
        }

        account.setPassword(passwordEncoder.encode(account.getPassword()));
        processAccountAuthorities(account);

        account = accountRepository.saveAndFlush(account);
        return account;
    }

    public Account findById(Long id) {
        return accountRepository.findOne(id);
    }

    public Account findByUserName(String name) {
        return accountRepository.findOneByUsername(name);
    }

    public boolean isAccountExist(Account account) {
        return accountRepository.findOneByUsername(account.getUsername()) != null;
    }

    public boolean isAccountExist(Long id) {
        return accountRepository.exists(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            UserDetails account = loadUserByUsernameSafely(username);
            if (account == null) {
                throw new UsernameNotFoundException("No user found with username: " + username);
            }
            return account;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public UserDetails loadUserByUsernameSafely(String username) {
        Account account = accountRepository.findOneByUsername(username);
        if (account == null) {
            return null;
        }
        return modelMapper.map(account, AccountDto.class);
    }


    @Transactional
    public void deleteAccountById(long id) {
        accountRepository.delete(id);
    }

    /**
     * As far as we don't want new Authorities to be created, we have to replace the given authorities
     * by the existing ones from the dictionary
     *
     * @param account Account to replace authorities for
     */
    private void processAccountAuthorities(Account account) {
        if (account.getAuthorities() == null) {
            return;
        }
        Set<AccountAuthority> newAuthorities = new HashSet<>();
        streamOf(account.getAuthorities()).forEach(accountAuthority -> {
            newAuthorities.add(authorityRepository.findOneByAuthority(accountAuthority.getAuthorityEnum()));
        });

        account.setAuthorities(newAuthorities);
    }
}
