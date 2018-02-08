package com.foodme;

import com.foodme.util.AbstractIntegrationTest;
import com.foodme.util.AuthHelper;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class TokenOperationsTest extends AbstractIntegrationTest {

    @Test
    public void givenUser_whenUseFoodmeClient_thenOkForExistingAccount() {
        final String accessToken = AuthHelper.obtainAccessToken("foodmeWeb", "admin@test.com", "tesT123");

        final Response accResponse = RestAssured.given().header("Authorization", "Bearer " + accessToken).
                get("http://localhost:9999/account/1");
        assertEquals(HttpStatus.OK.value(), accResponse.getStatusCode());
        assertNotNull(accResponse.jsonPath().get("username"));
    }

    @Test
    public void givenUser_whenUseFoodmeClient_thenNotFoundForNonExistingAccount() {
        final String accessToken = AuthHelper.obtainAccessToken("foodmeWeb", "admin@test.com", "tesT123");

        final Response accResponse = RestAssured.given().header("Authorization", "Bearer " + accessToken).
                get("http://localhost:9999/account/0");
        assertEquals(HttpStatus.NOT_FOUND.value(), accResponse.getStatusCode());
    }

    @Test
    public void givenUser_whenUseFoodmeClient_thenNewTokenIsReturnedByRefreshTokenRequest() {
        Response response = AuthHelper.getOauthResponse("foodmeWeb", "admin@test.com", "tesT123");
        final String accessToken = response.jsonPath().getString("access_token");

        assertNotNull(accessToken);

        final Map<String, String> params = new HashMap<String, String>();
        params.put("grant_type", "refresh_token");
        params.put("refresh_token", response.jsonPath().getString("refresh_token"));

        response = RestAssured.given().auth().preemptive().basic(
                "foodmeWeb", "bfe0ff72-66c7-48e8-9719-3a0cf9851015").and()
                .with().params(params).when().post("http://localhost:9999/oauth/token");

        final String newAccessToken = response.jsonPath().getString("access_token");
        assertNotNull(newAccessToken);
        assertNotEquals(accessToken, newAccessToken);
    }
}
