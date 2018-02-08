package com.foodme.service;

import com.foodme.model.Account;
import com.foodme.model.GeneralOrder;
import com.foodme.repository.AccountRepository;
import com.foodme.repository.GeneralOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
public class OrderService {

    private final AccountRepository accountRepository;
    private final GeneralOrderRepository generalOrderRepository;

    @Autowired
    public OrderService(AccountRepository accountRepository, GeneralOrderRepository generalOrderRepository) {
        this.accountRepository = accountRepository;
        this.generalOrderRepository = generalOrderRepository;
    }

    public boolean exists(Long id) {
        return generalOrderRepository.exists(id);
    }

    @Transactional
    public GeneralOrder save(GeneralOrder order, Long accountId) {
        Account account = accountRepository.findOne(accountId);
        if (account == null) {
            log.info("Account# {} does not exist", accountId);
        }
        return save(order, account);
    }

    @Transactional
    public GeneralOrder save(GeneralOrder order, Account account) {
        order.setCreatedBy(account);
        return generalOrderRepository.saveAndFlush(order);
    }

    @Transactional
    public GeneralOrder save(GeneralOrder order) {
        order = generalOrderRepository.saveAndFlush(order);
        return order;
    }

    public Optional<GeneralOrder> get(Long id) {
        return Optional.ofNullable(generalOrderRepository.findOne(id));
    }

    @Transactional
    public void delete(Long id) {
        generalOrderRepository.delete(id);
    }
}
