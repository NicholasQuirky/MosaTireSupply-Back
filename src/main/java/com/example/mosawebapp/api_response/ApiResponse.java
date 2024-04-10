package com.example.mosawebapp.api_response;

import org.springframework.http.HttpStatus;

public class ApiResponse {
  private HttpStatus status;
  private String message;
  public ApiResponse(String message, HttpStatus status){
    this.message = message;
    this.status = status;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public HttpStatus getStatus() {
    return status;
  }

  public void setStatus(HttpStatus status) {
    this.status = status;
  }
}
