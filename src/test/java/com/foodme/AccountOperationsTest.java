package com.foodme;

import com.foodme.dto.AccountDto;
import com.foodme.model.Account;
import com.foodme.util.AbstractIntegrationTest;
import com.foodme.util.SignUpResult;
import com.foodme.util.TestUtil;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.internal.mapper.ObjectMapperType;
import com.jayway.restassured.response.Response;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import static org.junit.Assert.*;


public class AccountOperationsTest extends AbstractIntegrationTest {
    @Test
    public void givenUser_whenUseFoodmeClient_thenCreatedForSuccessfulAccountCreation() {
        final Response accResponse = TestUtil.createAccount();
        assertEquals(HttpStatus.CREATED.value(), accResponse.getStatusCode());
        assertTrue(accResponse.getHeader("Location").matches(TestUtil.ACCOUNT_PATH_RETURNED_REGEXP));
    }

    @Test
    public void givenUser_whenUseFoodmeClient_thenNoContentForSuccessfulAccountDeletion() {
        final SignUpResult signUpResult = TestUtil.createAccountAndSignIn();
        assertNotNull(signUpResult.getAccessToken());
        assertNotNull(signUpResult.getAccountId());

        final Response accResponse = RestAssured.given()
                .header("Authorization", "Bearer " + signUpResult.getAccessToken())
                .header("Content-Type", "application/json")
                .delete("http://localhost:9999/account/" + signUpResult.getAccountId());

        assertEquals(HttpStatus.NO_CONTENT.value(), accResponse.getStatusCode());
    }

    @Test
    public void givenUser_whenUseFoodmeClient_thenHttpOKForSuccessfulAccountGetAndUpdate() {
        final SignUpResult signUpResult = TestUtil.createAccountAndSignIn();

        Response accResponse = RestAssured.given()
                .header("Authorization", "Bearer " + signUpResult.getAccessToken())
                .get("http://localhost:9999/account/" + signUpResult.getAccountId());

        AccountDto account = accResponse.as(AccountDto.class);

        assertNotNull(account);
        account.setFirstName("NewFirstName");

        accResponse = RestAssured.given()
                .header("Authorization", "Bearer " + signUpResult.getAccessToken())
                .header("Content-Type", "application/json")
                .body(account, ObjectMapperType.JACKSON_1)
                .put("http://localhost:9999/account/" + signUpResult.getAccountId());

        assertEquals(HttpStatus.OK.value(), accResponse.getStatusCode());

        accResponse = RestAssured.given()
                .header("Authorization", "Bearer " + signUpResult.getAccessToken())
                .get("http://localhost:9999/account/" + signUpResult.getAccountId());

        account = accResponse.as(AccountDto.class);

        assertEquals("NewFirstName", account.getFirstName());
    }
}
