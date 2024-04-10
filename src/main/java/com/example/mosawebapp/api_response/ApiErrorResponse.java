package com.example.mosawebapp.api_response;

import org.springframework.http.HttpStatus;

public class ApiErrorResponse {
  private String timestamp;
  private String status;
  private HttpStatus error;
  private String message;

  public ApiErrorResponse(String timestamp, String status, HttpStatus error, String message) {
    this.timestamp = timestamp;
    this.status = status;
    this.error = error;
    this.message = message;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public HttpStatus getError() {
    return error;
  }

  public void setError(HttpStatus error) {
    this.error = error;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
