package com.example.mosawebapp.account.dto;

public class OtpForm {
  private String otp;

  public OtpForm(){}
  public OtpForm(String otp) {
    this.otp = otp;
  }

  public String getOtp() {
    return otp;
  }

  public void setOtp(String otp) {
    this.otp = otp;
  }
}
