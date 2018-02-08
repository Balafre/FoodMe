package com.foodme.controller;

import com.foodme.dto.AccountDto;
import com.foodme.dto.MenuDto;
import com.foodme.dto.RestaurantDto;
import com.foodme.model.Account;
import com.foodme.model.Menu;
import com.foodme.model.Restaurant;
import com.foodme.service.AccountService;
import com.foodme.service.RestaurantService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Type;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;

@RestController
class AccountController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private AccountService accountService;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private ModelMapper modelMapper;


    @RequestMapping(value = "account/current", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public AccountDto currentAccount(Principal principal) {
        Assert.notNull(principal);
        return modelMapper.map(accountService.findByUserName(principal.getName()), AccountDto.class);
    }

    @RequestMapping(value = "account/{id}",
            method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AccountDto> getAccount(@PathVariable("id") Long id) {
        Account account = accountService.findById(id);
        if (account == null) {
            LOGGER.info("Account with id {} not found", id);
            return new ResponseEntity<AccountDto>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<AccountDto>(modelMapper.map(account, AccountDto.class), HttpStatus.OK);
    }

    @RequestMapping(value = "/account", method = RequestMethod.POST)
    public ResponseEntity<Void> createAccount(@RequestBody Account account, UriComponentsBuilder ucBuilder) {
        LOGGER.debug("Creating Account for: {}", account.getUsername());

        if (accountService.isAccountExist(account)) {
            LOGGER.info("Account already exists: {}", account.getUsername());
            return new ResponseEntity<Void>(HttpStatus.CONFLICT);
        }

        if (accountService.save(account) == null) {
            return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/account/{id}").buildAndExpand(account.getId()).toUri());
        headers.setAccessControlExposeHeaders(Arrays.asList("Location"));

        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/account/{id}", method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AccountDto> updateAccount(@PathVariable("id") long id, @RequestBody Account account) {
        LOGGER.debug("Updating Account with ID#{}", id);

        Account currentAccount = accountService.findById(id);

        if (currentAccount == null) {
            LOGGER.info("Account with id#{} not found", id);
            return new ResponseEntity<AccountDto>(HttpStatus.NOT_FOUND);
        }

        currentAccount = accountService.save(account);
        return new ResponseEntity<AccountDto>(modelMapper.map(currentAccount, AccountDto.class), HttpStatus.OK);
    }

    @RequestMapping(value = "/account/{id}", method = RequestMethod.DELETE)
//    @Secured("ROLE_ADMIN")
    public ResponseEntity<Void> deleteAccount(@PathVariable("id") long id) {
        LOGGER.debug("Fetching & Deleting Account with id#{}", id);

        Account account = accountService.findById(id);
        if (account == null) {
            LOGGER.info("Unable to delete. Account with id#{} not found", id);
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }

        accountService.deleteAccountById(id);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/account/{id}/restaurants",
            method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<RestaurantDto>> getRestaurantByAccount (@PathVariable("id") Long id) {
        LOGGER.debug("Fetching all restaurants for the Account with id#{}", id);

        if(!accountService.isAccountExist(id)) {
            LOGGER.info("Unable to fetch Account with id#{}: not found", id);
            return new ResponseEntity<List<RestaurantDto>>(HttpStatus.NOT_FOUND);
        }

        List<Restaurant> result = restaurantService.findAllRestaurantsByAccount(id);

        if(result == null) {
            return new ResponseEntity<List<RestaurantDto>>(HttpStatus.NO_CONTENT);
        }

        Type targetListType = new TypeToken<List<RestaurantDto>>(){}.getType();
        List<RestaurantDto> convertedResult = modelMapper.map(result, targetListType);

        return new ResponseEntity<List<RestaurantDto>>(convertedResult, HttpStatus.OK);
    }
}
