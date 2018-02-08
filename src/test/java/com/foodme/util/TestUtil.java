package com.foodme.util;

import com.foodme.enumeration.Authority;
import com.foodme.model.Account;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.internal.mapper.ObjectMapperType;
import com.jayway.restassured.response.Response;
import org.springframework.http.HttpStatus;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Lev on 6/3/2016.
 */
public class TestUtil {
    public static final String ACCOUNT_PATH_RETURNED_REGEXP = "http.?://localhost:9999/account/(\\d{1,})";
    public static final String RESTAURANT_PATH_RETURNED_REGEXP =
            "http.?://localhost:9999/restaurant/(\\d{1,})";
    public static final Pattern ACCOUNT_PATH_RETURNED_PATTERN = Pattern.compile(ACCOUNT_PATH_RETURNED_REGEXP);

    private TestUtil() {}

    public static Account generateAccount() {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        Account newAccount = new Account("user_" + uuid + "@foodme.com", "password");
        newAccount.setFirstName("FirstName");
        newAccount.setLastName("LastName");
        newAccount.setEnabled(true);
        newAccount.addAuthority(Authority.ROLE_USER);

        return newAccount;
    }

    public static Response createAccount() {
        return createAccount(generateAccount());
    }

    public static Response createAccount(Account account) {
        return RestAssured.given().header("Content-Type", "application/json")
                .body(account, ObjectMapperType.JACKSON_1)
                .post("http://localhost:9999/account");
    }

    public static SignUpResult createAccountAndSignIn() {
        final Account account = generateAccount();
        account.addAuthority(Authority.ROLE_ADMIN);
        Response accResponse = createAccount(account);

        final String location = accResponse.getHeader("Location");

        final Matcher m = ACCOUNT_PATH_RETURNED_PATTERN.matcher(location);
        assertTrue(m.find());
        final String newAccId = m.group(1);

        final String accessToken = AuthHelper.obtainAccessToken(
                "foodmeWeb", account.getUsername(), account.getPassword());

        return new SignUpResult(accessToken, newAccId);
    }

    public static void assertResponseOk(Response response) {
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
    }
}
