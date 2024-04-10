package com.example.mosawebapp.api_response;

import org.springframework.http.HttpStatus;

public class ApiChangePasswordResponse {
  private HttpStatus status;
  private String message;
  private String token;

  public ApiChangePasswordResponse(){}

  public ApiChangePasswordResponse(HttpStatus status, String message, String token) {
    this.status = status;
    this.message = message;
    this.token = token;
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

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}
