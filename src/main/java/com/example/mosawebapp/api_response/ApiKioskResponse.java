package com.example.mosawebapp.api_response;

import org.springframework.http.HttpStatus;

public class ApiKioskResponse {
  private HttpStatus status;
  private String kioskToken;

  public ApiKioskResponse(HttpStatus status, String kioskToken) {
    this.status = status;
    this.kioskToken = kioskToken;
  }

  public HttpStatus getStatus() {
    return status;
  }

  public void setStatus(HttpStatus status) {
    this.status = status;
  }

  public String getKioskToken() {
    return kioskToken;
  }

  public void setKioskToken(String kioskToken) {
    this.kioskToken = kioskToken;
  }
}
