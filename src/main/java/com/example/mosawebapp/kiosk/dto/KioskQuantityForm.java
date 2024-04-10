package com.example.mosawebapp.kiosk.dto;

public class KioskQuantityForm {
  private String kioskToken;
  private String orderId;

  public KioskQuantityForm(){}

  public KioskQuantityForm(String kioskToken, String orderId) {
    this.kioskToken = kioskToken;
    this.orderId = orderId;
  }

  public String getKioskToken() {
    return kioskToken;
  }

  public void setKioskToken(String kioskToken) {
    this.kioskToken = kioskToken;
  }

  public String getOrderId() {
    return orderId;
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }
}
