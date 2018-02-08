package com.foodme.util;

import lombok.Data;


@Data
public class SignUpResult {
    private String accessToken;
    private String accountId;

    public SignUpResult(String accessToken, String accountId) {
        this.accessToken = accessToken;
        this.accountId = accountId;
    }
}
