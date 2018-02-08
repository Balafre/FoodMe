package com.foodme.util;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lev on 5/24/2016.
 */
public class AuthHelper {
    public static String obtainAccessToken(String clientId, String username, String password) {
        final Response response = getOauthResponse(clientId, username, password);
        return response.jsonPath().getString("access_token");
    }

    public static Response getOauthResponse (String clientId, String username, String password) {
        final Map<String, String> params = new HashMap<String, String>();
        params.put("grant_type", "password");
        params.put("client_id", clientId);
        params.put("username", username);
        params.put("password", password);
        return RestAssured.given().auth().preemptive().basic(
                clientId, "bfe0ff72-66c7-48e8-9719-3a0cf9851015").and().
                with().params(params).when().post("http://localhost:9999/oauth/token");
    }
}
