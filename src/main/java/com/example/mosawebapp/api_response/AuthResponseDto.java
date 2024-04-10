package com.example.mosawebapp.api_response;

import com.example.mosawebapp.account.dto.AccountDto;
import org.springframework.http.HttpStatus;

public class AuthResponseDto {
    private HttpStatus status;
    private String message;
    private String accessToken;
    private AccountDto account;

    public AuthResponseDto(HttpStatus status, AccountDto account, String accessToken, String message) {
        this.status = status;
        this.account = account;
        this.accessToken = accessToken;
        this.message = message;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AccountDto getAccountDto() {
        return account;
    }

    public void setAccountDto(AccountDto account) {
        this.account = account;
    }
}
